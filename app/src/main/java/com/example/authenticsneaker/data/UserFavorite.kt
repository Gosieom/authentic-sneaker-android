package com.example.authenticsneaker.data
data class UserFavorite(
    val id: String = "",
    val userId: String = "",
    val sneakerId: String = "",
    val sneakerName: String = "",
    val sneakerImageUrl: String = "",
    val sneakerPrice: Double = 0.0,
    val addedAt: Long = System.currentTimeMillis()
) 