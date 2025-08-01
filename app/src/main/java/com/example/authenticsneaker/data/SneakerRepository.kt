package com.example.authenticsneaker.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class SneakerRepository {
    private val database = FirebaseDatabase.getInstance()
    private val sneakersRef = database.getReference("sneakers")
    private val favoritesRef = database.getReference("user_favorites")
    private val cartRef = database.getReference("user_cart")
    
    // ========== SNEAKER CRUD OPERATIONS ==========
    
    // CREATE - Add new sneaker
    suspend fun addSneaker(sneaker: Sneaker): Result<Sneaker> {
        return try {
            val sneakerId = sneakersRef.push().key ?: throw Exception("Failed to generate key")
            val sneakerWithId = sneaker.copy(id = sneakerId)
            sneakersRef.child(sneakerId).setValue(sneakerWithId).await()
            Result.success(sneakerWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // READ - Get all sneakers
    fun getAllSneakers(): Flow<List<Sneaker>> = flow {
        try {
            // Try to load from Realtime Database first
            try {
                val snapshot = sneakersRef.get().await()
                val sneakers = mutableListOf<Sneaker>()
                
                for (childSnapshot in snapshot.children) {
                    val sneaker = childSnapshot.getValue(Sneaker::class.java)
                    if (sneaker != null) {
                        sneakers.add(sneaker.copy(id = childSnapshot.key ?: ""))
                    }
                }
                
                // If we have database data, emit it
                if (sneakers.isNotEmpty()) {
                    emit(sneakers)
                } else {
                    // If database is empty, emit demo data
                    emit(DemoData.demoSneakers)
                }
            } catch (e: Exception) {
                // Database error - fallback to demo data
                emit(DemoData.demoSneakers)
            }
        } catch (e: Exception) {
            // Fallback to demo data
            emit(DemoData.demoSneakers)
        }
    }
    
    // READ - Get sneaker by ID
    suspend fun getSneakerById(id: String): Result<Sneaker> {
        return try {
            val snapshot = sneakersRef.child(id).get().await()
            if (snapshot.exists()) {
                val sneaker = snapshot.getValue(Sneaker::class.java)
                if (sneaker != null) {
                    Result.success(sneaker.copy(id = snapshot.key ?: ""))
                } else {
                    Result.failure(Exception("Failed to parse sneaker data"))
                }
            } else {
                Result.failure(Exception("Sneaker not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // READ - Search sneakers
    fun searchSneakers(query: String): Flow<List<Sneaker>> = flow {
        try {
            val snapshot = sneakersRef.get().await()
            val sneakers = mutableListOf<Sneaker>()
            
            for (childSnapshot in snapshot.children) {
                val sneaker = childSnapshot.getValue(Sneaker::class.java)
                if (sneaker != null && sneaker.name.contains(query, ignoreCase = true)) {
                    sneakers.add(sneaker.copy(id = childSnapshot.key ?: ""))
                }
            }
            emit(sneakers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    // READ - Get sneakers by brand
    fun getSneakersByBrand(brand: String): Flow<List<Sneaker>> = flow {
        try {
            // Filter demo data by brand
            val filteredSneakers = DemoData.demoSneakers.filter { 
                it.brand.equals(brand, ignoreCase = true) 
            }
            emit(filteredSneakers)
            
            // Try to load from database
            try {
                val snapshot = sneakersRef.get().await()
                val sneakers = mutableListOf<Sneaker>()
                
                for (childSnapshot in snapshot.children) {
                    val sneaker = childSnapshot.getValue(Sneaker::class.java)
                    if (sneaker != null && sneaker.brand.equals(brand, ignoreCase = true)) {
                        sneakers.add(sneaker.copy(id = childSnapshot.key ?: ""))
                    }
                }
                
                if (sneakers.isNotEmpty()) {
                    emit(sneakers)
                }
            } catch (e: Exception) {
                // Database error - demo data is already emitted
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    // UPDATE - Update sneaker
    suspend fun updateSneaker(sneaker: Sneaker): Result<Sneaker> {
        return try {
            sneakersRef.child(sneaker.id).setValue(sneaker).await()
            Result.success(sneaker)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // DELETE - Delete sneaker
    suspend fun deleteSneaker(sneakerId: String): Result<Unit> {
        return try {
            sneakersRef.child(sneakerId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== FAVORITES CRUD OPERATIONS ==========
    
    // CREATE - Add to favorites
    suspend fun addToFavorites(userId: String, sneaker: Sneaker): Result<UserFavorite> {
        return try {
            val favoriteId = favoritesRef.push().key ?: throw Exception("Failed to generate key")
            val favorite = UserFavorite(
                id = favoriteId,
                userId = userId,
                sneakerId = sneaker.id,
                sneakerName = sneaker.name,
                sneakerImageUrl = sneaker.imageUrl,
                sneakerPrice = sneaker.price
            )
            favoritesRef.child(favoriteId).setValue(favorite).await()
            Result.success(favorite)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // READ - Get user favorites
    fun getUserFavorites(userId: String): Flow<List<UserFavorite>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<UserFavorite>()
                for (childSnapshot in snapshot.children) {
                    val favorite = childSnapshot.getValue(UserFavorite::class.java)
                    if (favorite != null && favorite.userId == userId) {
                        favorites.add(favorite.copy(id = childSnapshot.key ?: ""))
                    }
                }
                trySend(favorites)
            }
            
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        
        favoritesRef.addValueEventListener(listener)
        awaitClose { favoritesRef.removeEventListener(listener) }
    }
    
    // READ - Check if sneaker is favorited
    suspend fun isFavorited(userId: String, sneakerId: String): Boolean {
        return try {
            val snapshot = favoritesRef.get().await()
            for (childSnapshot in snapshot.children) {
                val favorite = childSnapshot.getValue(UserFavorite::class.java)
                if (favorite != null && favorite.userId == userId && favorite.sneakerId == sneakerId) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }
    
    // DELETE - Remove from favorites
    suspend fun removeFromFavorites(userId: String, sneakerId: String): Result<Unit> {
        return try {
            val snapshot = favoritesRef.get().await()
            for (childSnapshot in snapshot.children) {
                val favorite = childSnapshot.getValue(UserFavorite::class.java)
                if (favorite != null && favorite.userId == userId && favorite.sneakerId == sneakerId) {
                    favoritesRef.child(childSnapshot.key ?: "").removeValue().await()
                    break
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // DELETE - Remove favorite by ID
    suspend fun removeFavoriteById(favoriteId: String): Result<Unit> {
        return try {
            favoritesRef.child(favoriteId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== CART CRUD OPERATIONS ==========
    
    // CREATE - Add to cart
    suspend fun addToCart(userId: String, sneaker: Sneaker, size: String): Result<CartItem> {
        return try {
            val cartItemId = cartRef.push().key ?: throw Exception("Failed to generate key")
            val cartItem = CartItem(
                id = cartItemId,
                userId = userId,
                sneakerId = sneaker.id,
                sneakerName = sneaker.name,
                sneakerImageUrl = sneaker.imageUrl,
                sneakerPrice = sneaker.price,
                size = size,
                quantity = 1
            )
            cartRef.child(cartItemId).setValue(cartItem).await()
            Result.success(cartItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // READ - Get user cart
    fun getUserCart(userId: String): Flow<List<CartItem>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartItem>()
                for (childSnapshot in snapshot.children) {
                    val cartItem = childSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null && cartItem.userId == userId) {
                        cartItems.add(cartItem.copy(id = childSnapshot.key ?: ""))
                    }
                }
                trySend(cartItems)
            }
            
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        
        cartRef.addValueEventListener(listener)
        awaitClose { cartRef.removeEventListener(listener) }
    }
    
    // READ - Check if item is in cart
    suspend fun isInCart(userId: String, sneakerId: String): Boolean {
        return try {
            val snapshot = cartRef.get().await()
            for (childSnapshot in snapshot.children) {
                val cartItem = childSnapshot.getValue(CartItem::class.java)
                if (cartItem != null && cartItem.userId == userId && cartItem.sneakerId == sneakerId) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }
    
    // UPDATE - Update cart item quantity
    suspend fun updateCartItemQuantity(cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            cartRef.child(cartItemId).child("quantity").setValue(quantity).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // DELETE - Remove from cart
    suspend fun removeFromCart(userId: String, sneakerId: String): Result<Unit> {
        return try {
            val snapshot = cartRef.get().await()
            for (childSnapshot in snapshot.children) {
                val cartItem = childSnapshot.getValue(CartItem::class.java)
                if (cartItem != null && cartItem.userId == userId && cartItem.sneakerId == sneakerId) {
                    cartRef.child(childSnapshot.key ?: "").removeValue().await()
                    break
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // DELETE - Remove cart item by ID
    suspend fun removeCartItemById(cartItemId: String): Result<Unit> {
        return try {
            cartRef.child(cartItemId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // DELETE - Clear user cart
    suspend fun clearUserCart(userId: String): Result<Unit> {
        return try {
            val snapshot = cartRef.get().await()
            for (childSnapshot in snapshot.children) {
                val cartItem = childSnapshot.getValue(CartItem::class.java)
                if (cartItem != null && cartItem.userId == userId) {
                    cartRef.child(childSnapshot.key ?: "").removeValue().await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 