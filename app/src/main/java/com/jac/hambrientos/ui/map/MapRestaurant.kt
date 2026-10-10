package com.jac.hambrientos.ui.map

import com.jac.hambrientos.data.local.demoProducts
import com.jac.hambrientos.data.local.demoRestaurants
import org.maplibre.android.geometry.LatLng

data class MapRestaurant(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val rating: Double,
    val distance: String,
    val position: LatLng,
    val imageUrl: String,
    val isUnderBs15: Boolean,
)

val demoMapRestaurants = demoRestaurants.map { restaurant ->
    MapRestaurant(
        id = restaurant.id.toString(),
        name = restaurant.name,
        category = restaurant.category,
        price = restaurant.priceRange,
        rating = restaurant.rating,
        distance = "${restaurant.distanceMeters} m",
        position = LatLng(restaurant.latitude, restaurant.longitude),
        imageUrl = restaurant.imageUrl,
        isUnderBs15 = demoProducts.any {
            it.restaurantId == restaurant.id && it.price < 15.0
        },
    )
}
