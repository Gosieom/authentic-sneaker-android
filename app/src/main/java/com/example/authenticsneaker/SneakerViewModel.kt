package com.example.authenticsneaker

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.authenticsneaker.data.Sneaker
import com.example.authenticsneaker.data.SneakerRepository
import com.example.authenticsneaker.data.UserFavorite
import com.example.authenticsneaker.data.CartItem
import com.example.authenticsneaker.data.LocalStorage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SneakerState(
    val sneakers: List<Sneaker> = emptyList(),
    val favorites: List<UserFavorite> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedBrand: String = ""
)

class SneakerViewModel(application: android.app.Application) : AndroidViewModel(application) {
    private val repository = SneakerRepository()
    private val auth = FirebaseAuth.getInstance()
    private val localStorage = LocalStorage(application)
    
    private val _state = MutableStateFlow(SneakerState())
    val state: StateFlow<SneakerState> = _state.asStateFlow()
    
    init {
        loadSneakers()
        // Initialize state from local storage first
        updateStateFromLocalStorage()
        // Then try to load from Firestore
        loadUserFavorites()
        loadUserCart()
        
        // Observe local storage changes
        viewModelScope.launch {
            localStorage.cartItems.collect { cartItems ->
                _state.value = _state.value.copy(cartItems = cartItems)
            }
        }
        
        viewModelScope.launch {
            localStorage.favorites.collect { favorites ->
                _state.value = _state.value.copy(favorites = favorites)
            }
        }
    }
    
    // ========== SNEAKER CRUD OPERATIONS ==========
    
    fun loadSneakers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                repository.getAllSneakers().collect { sneakers ->
                    _state.value = _state.value.copy(
                        sneakers = sneakers,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load sneakers"
                )
            }
        }
    }
    
    fun addSneaker(sneaker: Sneaker) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val result = repository.addSneaker(sneaker)
                if (result.isSuccess) {
                    loadSneakers() // Reload the list
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to add sneaker"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to add sneaker"
                )
            }
        }
    }
    
    fun updateSneaker(sneaker: Sneaker) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val result = repository.updateSneaker(sneaker)
                if (result.isSuccess) {
                    loadSneakers() // Reload the list
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to update sneaker"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update sneaker"
                )
            }
        }
    }
    
    fun deleteSneaker(sneakerId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val result = repository.deleteSneaker(sneakerId)
                if (result.isSuccess) {
                    loadSneakers() // Reload the list
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Failed to delete sneaker"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete sneaker"
                )
            }
        }
    }
    
    fun searchSneakers(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        
        if (query.isBlank()) {
            loadSneakers()
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                repository.searchSneakers(query).collect { sneakers ->
                    _state.value = _state.value.copy(
                        sneakers = sneakers,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to search sneakers"
                )
            }
        }
    }
    
    fun filterByBrand(brand: String) {
        _state.value = _state.value.copy(selectedBrand = brand)
        
        if (brand.isBlank()) {
            loadSneakers()
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                repository.getSneakersByBrand(brand).collect { sneakers ->
                    _state.value = _state.value.copy(
                        sneakers = sneakers,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to filter sneakers"
                )
            }
        }
    }
    
    // ========== FAVORITES CRUD OPERATIONS ==========
    
    fun loadUserFavorites() {
        val userId = auth.currentUser?.uid ?: "local_user"
        
        viewModelScope.launch {
            // Always initialize from local storage first
            _state.value = _state.value.copy(favorites = localStorage.favorites.value)
            try {
                repository.getUserFavorites(userId).collect { firestoreFavorites ->
                    if (firestoreFavorites.isNotEmpty()) {
                        // If Realtime Database has data, use it
                        _state.value = _state.value.copy(favorites = firestoreFavorites)
                    } else {
                        // If Realtime Database is empty, ensure local storage state is maintained
                        _state.value = _state.value.copy(favorites = localStorage.favorites.value)
                    }
                }
            } catch (e: Exception) {
                // If Realtime Database fails, local storage state is already set
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to load favorites from Realtime Database, using local data."
                )
            }
        }
    }
    
    fun addToFavorites(sneaker: Sneaker) {
        val userId = auth.currentUser?.uid ?: "local_user"
        
        viewModelScope.launch {
            try {
                // Try Firestore first
                val result = repository.addToFavorites(userId, sneaker)
                if (result.isSuccess) {
                    loadUserFavorites() // Reload favorites
                } else {
                    // If Firestore fails, use local storage
                    val favorite = UserFavorite(
                        userId = userId,
                        sneakerId = sneaker.id,
                        sneakerName = sneaker.name,
                        sneakerImageUrl = sneaker.imageUrl,
                        sneakerPrice = sneaker.price
                    )
                    localStorage.addToFavorites(favorite)
                    updateStateFromLocalStorage()
                }
            } catch (e: Exception) {
                // If there's an exception, use local storage
                val favorite = UserFavorite(
                    userId = userId,
                    sneakerId = sneaker.id,
                    sneakerName = sneaker.name,
                    sneakerImageUrl = sneaker.imageUrl,
                    sneakerPrice = sneaker.price
                )
                localStorage.addToFavorites(favorite)
                updateStateFromLocalStorage()
            }
        }
    }
    
    fun removeFromFavorites(sneakerId: String) {
        val userId = auth.currentUser?.uid ?: "local_user"
        
        viewModelScope.launch {
            try {
                // Try Firestore first
                val result = repository.removeFromFavorites(userId, sneakerId)
                if (result.isSuccess) {
                    loadUserFavorites() // Reload favorites
                } else {
                    // If Firestore fails, use local storage
                    localStorage.removeFromFavoritesBySneakerId(sneakerId)
                    updateStateFromLocalStorage()
                }
            } catch (e: Exception) {
                // If there's an exception, use local storage
                localStorage.removeFromFavoritesBySneakerId(sneakerId)
                updateStateFromLocalStorage()
            }
        }
    }
    
    fun removeFavoriteById(favoriteId: String) {
        viewModelScope.launch {
            try {
                // Try Firestore first
                val result = repository.removeFavoriteById(favoriteId)
                if (result.isSuccess) {
                    loadUserFavorites() // Reload favorites
                } else {
                    // If Firestore fails, use local storage
                    localStorage.removeFromFavorites(favoriteId)
                    updateStateFromLocalStorage()
                }
            } catch (e: Exception) {
                // If there's an exception, use local storage
                localStorage.removeFromFavorites(favoriteId)
                updateStateFromLocalStorage()
            }
        }
    }
    
    fun isFavorited(sneakerId: String): Boolean {
        return localStorage.isFavorited(sneakerId)
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    fun clearFilters() {
        _state.value = _state.value.copy(
            searchQuery = "",
            selectedBrand = ""
        )
        loadSneakers()
    }
    
    // ========== CART CRUD OPERATIONS ==========
    
    fun loadUserCart() {
        val userId = auth.currentUser?.uid ?: "local_user"
        
        viewModelScope.launch {
            // Always initialize from local storage first
            _state.value = _state.value.copy(cartItems = localStorage.cartItems.value)
            try {
                repository.getUserCart(userId).collect { firestoreCartItems ->
                    if (firestoreCartItems.isNotEmpty()) {
                        // If Realtime Database has data, use it
                        _state.value = _state.value.copy(cartItems = firestoreCartItems)
                    }
                    // If Realtime Database is empty, keep the local storage data (don't override)
                }
            } catch (e: Exception) {
                // If Realtime Database fails, local storage state is already set
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to load cart from Realtime Database, using local data."
                )
            }
        }
    }
    
    fun addToCart(sneaker: Sneaker, size: String) {
        val userId = auth.currentUser?.uid ?: "local_user"
        
        viewModelScope.launch {
            // Always add to local storage first for immediate UI update
            val cartItem = CartItem(
                userId = userId,
                sneakerId = sneaker.id,
                sneakerName = sneaker.name,
                sneakerImageUrl = sneaker.imageUrl,
                sneakerPrice = sneaker.price,
                size = size
            )
            localStorage.addToCart(cartItem)
            updateStateFromLocalStorage()
            
            try {
                // Then try to add to Realtime Database
                val result = repository.addToCart(userId, sneaker, size)
                if (result.isSuccess) {
                    // Success - both local and remote are updated
                    println("Added to both local storage and Realtime Database")
                } else {
                    // Realtime Database failed, but local storage is already updated
                    println("Realtime Database failed, using local storage only")
                }
            } catch (e: Exception) {
                // Realtime Database failed, but local storage is already updated
                println("Realtime Database exception, using local storage only: ${e.message}")
            }
        }
    }
    
    fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                // Try Firestore first
                val result = repository.updateCartItemQuantity(cartItemId, quantity)
                if (result.isSuccess) {
                    loadUserCart() // Reload cart
                } else {
                    // If Firestore fails, use local storage
                    localStorage.updateCartItemQuantity(cartItemId, quantity)
                    updateStateFromLocalStorage()
                }
            } catch (e: Exception) {
                // If there's an exception, use local storage
                localStorage.updateCartItemQuantity(cartItemId, quantity)
                updateStateFromLocalStorage()
            }
        }
    }
    
    fun removeFromCart(sneakerId: String) {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            try {
                val result = repository.removeFromCart(userId, sneakerId)
                if (result.isSuccess) {
                    loadUserCart() // Reload cart
                } else {
                    _state.value = _state.value.copy(
                        error = result.exceptionOrNull()?.message ?: "Failed to remove from cart"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to remove from cart"
                )
            }
        }
    }
    
    fun removeCartItemById(cartItemId: String) {
        viewModelScope.launch {
            try {
                // Try Firestore first
                val result = repository.removeCartItemById(cartItemId)
                if (result.isSuccess) {
                    loadUserCart() // Reload cart
                } else {
                    // If Firestore fails, use local storage
                    localStorage.removeFromCart(cartItemId)
                    updateStateFromLocalStorage()
                }
            } catch (e: Exception) {
                // If there's an exception, use local storage
                localStorage.removeFromCart(cartItemId)
                updateStateFromLocalStorage()
            }
        }
    }
    
    fun clearCart() {
        val userId = auth.currentUser?.uid ?: "local_user"
        
        viewModelScope.launch {
            try {
                // Try Firestore first
                val result = repository.clearUserCart(userId)
                if (result.isSuccess) {
                    loadUserCart() // Reload cart
                } else {
                    // If Firestore fails, use local storage
                    localStorage.clearCart()
                    updateStateFromLocalStorage()
                }
            } catch (e: Exception) {
                // If there's an exception, use local storage
                localStorage.clearCart()
                updateStateFromLocalStorage()
            }
        }
    }
    
    fun clearFavorites() {
        viewModelScope.launch {
            try {
                // Try Firestore first (would need to implement this in repository)
                // For now, just use local storage
                localStorage.clearFavorites()
                updateStateFromLocalStorage()
            } catch (e: Exception) {
                // If there's an exception, use local storage
                localStorage.clearFavorites()
                updateStateFromLocalStorage()
            }
        }
    }
    
    fun isInCart(sneakerId: String): Boolean {
        // Use state cart items if available, otherwise fall back to localStorage
        return if (_state.value.cartItems.isNotEmpty()) {
            _state.value.cartItems.any { it.sneakerId == sneakerId }
        } else {
            localStorage.isInCart(sneakerId)
        }
    }
    
    fun getCartTotal(): Double {
        // Use state cart items if available, otherwise fall back to localStorage
        return if (_state.value.cartItems.isNotEmpty()) {
            _state.value.cartItems.sumOf { it.getTotalPrice() }
        } else {
            localStorage.getCartTotal()
        }
    }
    
    fun getCartItemCount(): Int {
        // Use state cart items if available, otherwise fall back to localStorage
        return if (_state.value.cartItems.isNotEmpty()) {
            _state.value.cartItems.size
        } else {
            localStorage.getCartItemCount()
        }
    }
    
    private fun updateStateFromLocalStorage() {
        _state.value = _state.value.copy(
            cartItems = localStorage.cartItems.value,
            favorites = localStorage.favorites.value
        )
    }
    
    // Force refresh state from local storage
    fun refreshFromLocalStorage() {
        updateStateFromLocalStorage()
    }
    

}

class ViewModelFactory(private val application: android.app.Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SneakerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SneakerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 