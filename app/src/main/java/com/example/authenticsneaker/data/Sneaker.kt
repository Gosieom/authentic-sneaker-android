package com.example.authenticsneaker.data
data class Sneaker(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val model: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val size: String = "",
    val color: String = "",
    val condition: String = "", // New, Used, Like New
    val releaseDate: String = "",
    val inStock: Boolean = true,
    val stockQuantity: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sellerId: String = "",
    val sellerName: String = ""
) {
    fun isOnSale(): Boolean = price < originalPrice
    
    fun getDiscountPercentage(): Int {
        return if (originalPrice > 0) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else 0
    }
    
    fun getFormattedPrice(): String = "$${String.format("%.2f", price)}"
    
    fun getFormattedOriginalPrice(): String = "$${String.format("%.2f", originalPrice)}"
} 