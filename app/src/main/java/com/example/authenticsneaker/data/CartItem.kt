package com.example.authenticsneaker.data
data class CartItem(
    val id: String = "",
    val userId: String = "",
    val sneakerId: String = "",
    val sneakerName: String = "",
    val sneakerImageUrl: String = "",
    val sneakerPrice: Double = 0.0,
    val quantity: Int = 1,
    val size: String = "",
    val addedAt: Long = System.currentTimeMillis()
) {
    fun getTotalPrice(): Double = sneakerPrice * quantity
    
    fun getFormattedTotalPrice(): String = "$${String.format("%.2f", getTotalPrice())}"
} 