package com.example.authenticsneaker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("authentic_sneaker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(loadCartItems())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _favorites = MutableStateFlow<List<UserFavorite>>(loadFavorites())
    val favorites: StateFlow<List<UserFavorite>> = _favorites.asStateFlow()
    
    private var cartItemIdCounter = loadCartItemIdCounter()
    private var favoriteIdCounter = loadFavoriteIdCounter()
    
    // Cart operations
    fun addToCart(cartItem: CartItem): CartItem {
        val newCartItem = cartItem.copy(id = "local_${cartItemIdCounter++}")
        val currentItems = _cartItems.value.toMutableList()
        currentItems.add(newCartItem)
        _cartItems.value = currentItems
        saveCartItems(currentItems)
        saveCartItemIdCounter(cartItemIdCounter)
        return newCartItem
    }
    
    fun removeFromCart(cartItemId: String) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.id == cartItemId }
        _cartItems.value = currentItems
        saveCartItems(currentItems)
    }
    
    fun updateCartItemQuantity(cartItemId: String, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == cartItemId }
        if (index != -1) {
            currentItems[index] = currentItems[index].copy(quantity = quantity)
            _cartItems.value = currentItems
            saveCartItems(currentItems)
        }
    }
    
    fun clearCart() {
        _cartItems.value = emptyList()
        saveCartItems(emptyList())
    }
    
    fun isInCart(sneakerId: String): Boolean {
        return _cartItems.value.any { it.sneakerId == sneakerId }
    }
    
    fun getCartItemCount(): Int {
        return _cartItems.value.size
    }
    
    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.getTotalPrice() }
    }
    
    // Favorites operations
    fun addToFavorites(favorite: UserFavorite): UserFavorite {
        val newFavorite = favorite.copy(id = "local_${favoriteIdCounter++}")
        val currentFavorites = _favorites.value.toMutableList()
        currentFavorites.add(newFavorite)
        _favorites.value = currentFavorites
        saveFavorites(currentFavorites)
        saveFavoriteIdCounter(favoriteIdCounter)
        return newFavorite
    }
    
    fun removeFromFavorites(favoriteId: String) {
        val currentFavorites = _favorites.value.toMutableList()
        currentFavorites.removeAll { it.id == favoriteId }
        _favorites.value = currentFavorites
        saveFavorites(currentFavorites)
    }
    
    fun removeFromFavoritesBySneakerId(sneakerId: String) {
        val currentFavorites = _favorites.value.toMutableList()
        currentFavorites.removeAll { it.sneakerId == sneakerId }
        _favorites.value = currentFavorites
        saveFavorites(currentFavorites)
    }
    
    fun clearFavorites() {
        _favorites.value = emptyList()
        saveFavorites(emptyList())
    }
    
    fun isFavorited(sneakerId: String): Boolean {
        return _favorites.value.any { it.sneakerId == sneakerId }
    }
    
    // Persistence methods
    private fun saveCartItems(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString("cart_items", json).apply()
    }
    
    private fun loadCartItems(): List<CartItem> {
        val json = prefs.getString("cart_items", "[]")
        val type = object : TypeToken<List<CartItem>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveFavorites(items: List<UserFavorite>) {
        val json = gson.toJson(items)
        prefs.edit().putString("favorites", json).apply()
    }
    
    private fun loadFavorites(): List<UserFavorite> {
        val json = prefs.getString("favorites", "[]")
        val type = object : TypeToken<List<UserFavorite>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveCartItemIdCounter(counter: Int) {
        prefs.edit().putInt("cart_item_id_counter", counter).apply()
    }
    
    private fun loadCartItemIdCounter(): Int {
        return prefs.getInt("cart_item_id_counter", 1)
    }
    
    private fun saveFavoriteIdCounter(counter: Int) {
        prefs.edit().putInt("favorite_id_counter", counter).apply()
    }
    
    private fun loadFavoriteIdCounter(): Int {
        return prefs.getInt("favorite_id_counter", 1)
    }
} 