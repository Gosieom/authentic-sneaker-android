package com.example.authenticsneaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val signUpSuccess: Boolean = false,
    val isAdmin: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        // Listen for authentication state changes
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user ->
                println("AuthViewModel: User authenticated - ${user.email}")
                val isAdmin = user.email == "admin@gmail.com"
                _authState.value = _authState.value.copy(
                    user = user,
                    isAuthenticated = true,
                    isAdmin = isAdmin
                )
            } ?: run {
                println("AuthViewModel: No user authenticated")
                _authState.value = _authState.value.copy(
                    user = null,
                    isAuthenticated = false,
                    isAdmin = false
                )
            }
        }
    }
    
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    val isAdmin = user.email == "admin@gmail.com"
                    _authState.value = _authState.value.copy(
                        user = user,
                        isAuthenticated = true,
                        isLoading = false,
                        isAdmin = isAdmin
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Authentication failed"
                )
            }
        }
    }
    
    fun signUpWithEmail(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    // Update user profile with full name
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                    
                    user.updateProfile(profileUpdates).await()
                    
                    val isAdmin = user.email == "admin@gmail.com"
                    _authState.value = _authState.value.copy(
                        user = user,
                        isAuthenticated = true,
                        isLoading = false,
                        signUpSuccess = true,
                        isAdmin = isAdmin
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed"
                )
            }
        }
    }
    
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                
                result.user?.let { user ->
                    val isAdmin = user.email == "admin@gmail.com"
                    _authState.value = _authState.value.copy(
                        user = user,
                        isAuthenticated = true,
                        isLoading = false,
                        isAdmin = isAdmin
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Google Sign-In failed"
                )
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState()
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
    
    fun clearSignUpSuccess() {
        _authState.value = _authState.value.copy(signUpSuccess = false)
    }
    
    fun resetPassword(email: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message ?: "Password reset failed")
            }
        }
    }
} 