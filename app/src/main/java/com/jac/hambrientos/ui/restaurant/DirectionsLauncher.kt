package com.jac.hambrientos.ui.restaurant

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun openDirections(
    context: Context,
    latitude: Double,
    longitude: Double,
    restaurantName: String,
): Boolean {
    val coordinates = "$latitude,$longitude"
    val encodedName = Uri.encode(restaurantName)
    val mapIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("geo:$coordinates?q=$coordinates($encodedName)"),
    ).forContext(context)

    if (mapIntent.resolveActivity(context.packageManager) != null) {
        try {
            context.startActivity(mapIntent)
            return true
        } catch (_: ActivityNotFoundException) {
            // The selected handler disappeared; continue with the web fallback.
        }
    }

    val openStreetMapIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(
            "https://www.openstreetmap.org/?mlat=$latitude&mlon=$longitude" +
                "#map=18/$latitude/$longitude",
        ),
    ).forContext(context)

    if (openStreetMapIntent.resolveActivity(context.packageManager) != null) {
        try {
            context.startActivity(openStreetMapIntent)
            return true
        } catch (_: ActivityNotFoundException) {
            // No browser is available either.
        }
    }

    return false
}

private fun Intent.forContext(context: Context): Intent = apply {
    if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
