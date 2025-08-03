package com.example.authenticsneaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState

@Composable
fun AuthNavigation() {
    var currentScreen by rememberSaveable { mutableStateOf("splash") }
    var sneakerViewModel by remember { mutableStateOf<SneakerViewModel?>(null) }
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    
    // Check authentication state on app start
    LaunchedEffect(Unit) {
        // Give Firebase a moment to initialize and check auth state
        kotlinx.coroutines.delay(1000)
        println("AuthNavigation: Checking auth state - isAuthenticated: ${authState.isAuthenticated}, currentScreen: $currentScreen")
        // Check if user is already authenticated when app starts
        if (authState.isAuthenticated && currentScreen == "splash") {
            // User is authenticated, skip login and go directly to home
            println("AuthNavigation: User authenticated, going to home")
            currentScreen = "home"
        }
    }
    
    // Check authentication state changes
    LaunchedEffect(authState.isAuthenticated) {
        if (!authState.isAuthenticated && currentScreen != "splash" && currentScreen != "login" && currentScreen != "signup") {
            currentScreen = "login"
        }
    }
    
    // Check admin status changes and redirect accordingly
    LaunchedEffect(authState.isAdmin) {
        if (authState.isAuthenticated && authState.isAdmin && currentScreen == "home") {
            currentScreen = "admin"
        }
    }
    
    when (currentScreen) {
        "splash" -> {
            SplashScreen(
                onSplashComplete = {
                    // Check if user is already authenticated
                    if (authState.isAuthenticated) {
                        if (authState.isAdmin) {
                            currentScreen = "admin"
                        } else {
                            currentScreen = "home"
                        }
                    } else {
                        currentScreen = "login"
                    }
                }
            )
        }
        "login" -> {
            LoginScreen(
                onLoginSuccess = {
                    if (authState.isAdmin) {
                        currentScreen = "admin"
                    } else {
                        currentScreen = "home"
                    }
                },
                onNavigateToSignUp = {
                    currentScreen = "signup"
                },
                onGoogleSignIn = {
                    // TODO: Implement Google Sign In
                    if (authState.isAdmin) {
                        currentScreen = "admin"
                    } else {
                        currentScreen = "home"
                    }
                },
                authViewModel = authViewModel
            )
        }
        "signup" -> {
            SignUpScreen(
                onSignUpSuccess = {
                    currentScreen = "login"
                },
                onNavigateToLogin = {
                    currentScreen = "login"
                },
                onGoogleSignIn = {
                    // TODO: Implement Google Sign In
                    if (authState.isAdmin) {
                        currentScreen = "admin"
                    } else {
                        currentScreen = "home"
                    }
                },
                authViewModel = authViewModel
            )
        }
        "home" -> {
            if (sneakerViewModel == null) {
                sneakerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))
            }
            MarketplaceScreen(
                onNavigateToDetail = { sneakerId ->
                    // TODO: Navigate to sneaker detail
                },
                onNavigateToCart = {
                    currentScreen = "cart"
                },
                onNavigateToFavorites = {
                    currentScreen = "favorites"
                },
                onNavigateToProfile = {
                    currentScreen = "profile"
                },
                sneakerViewModel = sneakerViewModel!!
            )
        }
        "cart" -> {
            if (sneakerViewModel == null) {
                sneakerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))
            }
            CartScreen(
                onNavigateBack = {
                    currentScreen = "home"
                },
                onCheckout = {
                    currentScreen = "checkout"
                },
                sneakerViewModel = sneakerViewModel!!
            )
        }
        "favorites" -> {
            if (sneakerViewModel == null) {
                sneakerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))
            }
            FavoritesScreen(
                onNavigateBack = {
                    currentScreen = "home"
                },
                onNavigateToDetail = { sneakerId ->
                    // TODO: Navigate to sneaker detail
                },
                sneakerViewModel = sneakerViewModel!!
            )
        }
        "checkout" -> {
            if (sneakerViewModel == null) {
                sneakerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))
            }
            CheckoutScreen(
                onNavigateBack = {
                    currentScreen = "cart"
                },
                onOrderComplete = {
                    // Clear cart and go back to home
                    sneakerViewModel!!.clearCart()
                    currentScreen = "home"
                },
                sneakerViewModel = sneakerViewModel!!
            )
        }
        "profile" -> {
            ProfileScreen(
                onNavigateBack = {
                    currentScreen = "home"
                },
                onLogout = {
                    // Sign out and navigate to login
                    authViewModel.signOut()
                    currentScreen = "login"
                },
                onNavigateToAdmin = {
                    currentScreen = "admin"
                },
                authViewModel = authViewModel
            )
        }
        "admin" -> {
            if (sneakerViewModel == null) {
                sneakerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))
            }
            AdminScreen(
                onNavigateBack = {
                    // Go back to login since admin doesn't have a regular profile
                    authViewModel.signOut()
                    currentScreen = "login"
                },
                onNavigateToProfile = {
                    currentScreen = "admin_profile"
                },
                sneakerViewModel = sneakerViewModel!!
            )
        }
        "admin_profile" -> {
            AdminProfileScreen(
                onNavigateBack = {
                    currentScreen = "admin"
                },
                onLogout = {
                    authViewModel.signOut()
                    currentScreen = "login"
                },
                authViewModel = authViewModel
            )
        }
    }
}

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    
    // Placeholder home screen - replace with your actual app content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E1E),
                        Color(0xFF2D2D2D)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to AuthenticSneaker!",
                fontSize = 23.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Show user info if available
            authState.user?.let { user ->
                Text(
                    text = "Hello, ${user.displayName ?: user.email ?: "User"}!",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: ${user.email}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Button(
                onClick = {
                    authViewModel.signOut()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00D4FF)
                )
            ) {
                Text("Logout")
            }
        }
    }
} 