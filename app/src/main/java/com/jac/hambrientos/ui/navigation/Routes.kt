package com.jac.hambrientos.ui.navigation

import android.net.Uri

object Routes {
    const val Home = "home"
    const val Map = "map"
    const val Profile = "profile"
    const val Cart = "cart"
    const val Checkout = "checkout"
    const val Orders = "orders"
    const val RestaurantId = "restaurantId"
    const val OrderId = "orderId"
    const val MapDestination = "map?destination={$RestaurantId}"
    const val Restaurant = "restaurant/{$RestaurantId}"
    const val Rate = "rate/{$RestaurantId}"
    const val Order = "order/{$OrderId}"

    fun restaurant(restaurantId: String): String =
        "restaurant/${Uri.encode(restaurantId)}"

    fun map(restaurantId: String): String =
        "map?destination=${Uri.encode(restaurantId)}"

    fun rate(restaurantId: String): String =
        "rate/${Uri.encode(restaurantId)}"

    fun order(orderId: Int): String = "order/$orderId"
}
