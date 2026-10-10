package com.jac.hambrientos.data.local

data class Restaurant(
    val id: Int,
    val name: String,
    val category: String,
    val priceRange: String,
    val rating: Double,
    val distanceMeters: Int,
    val imageUrl: String,
    val hours: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
)

data class RestaurantProduct(
    val id: Int,
    val restaurantId: Int,
    val category: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
)

data class RestaurantPromotion(
    val id: Int,
    val restaurantId: Int,
    val title: String,
    val description: String,
    val originalPrice: Double?,
    val promoPrice: Double,
    val validToday: Boolean,
)

data class CartItem(
    val product: RestaurantProduct,
    val quantity: Int,
)
