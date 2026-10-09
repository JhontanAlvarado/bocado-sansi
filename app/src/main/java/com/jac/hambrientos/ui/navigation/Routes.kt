package com.jac.hambrientos.ui.navigation

import android.net.Uri

object Routes {
    const val Home = "home"
    const val Map = "map"
    const val Profile = "profile"
    const val RestaurantId = "restaurantId"
    const val Restaurant = "restaurant/{$RestaurantId}"
    const val Rate = "rate/{$RestaurantId}"

    fun restaurant(restaurantId: String): String =
        "restaurant/${Uri.encode(restaurantId)}"

    fun rate(restaurantId: String): String =
        "rate/${Uri.encode(restaurantId)}"
}
