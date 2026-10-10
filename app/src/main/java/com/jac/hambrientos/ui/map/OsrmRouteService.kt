package com.jac.hambrientos.ui.map

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.maplibre.android.geometry.LatLng

private const val OsrmBaseUrl = "https://router.project-osrm.org/route/v1"

/**
 * The public OSRM demo server is configured with the driving profile. The API's
 * profile path segment does not turn that server into a pedestrian router, so
 * this is deliberately named and surfaced in the UI as a driving reference.
 */
private const val PublicOsrmProfile = "driving"

class OsrmRouteService(
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend fun route(origin: LatLng, destination: LatLng): MapRoute = withContext(Dispatchers.IO) {
        val coordinates = "${origin.longitude},${origin.latitude};" +
            "${destination.longitude},${destination.latitude}"
        val connection = URL(
            "$OsrmBaseUrl/$PublicOsrmProfile/$coordinates" +
                "?overview=full&geometries=geojson",
        ).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 20_000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "Hambrientos-Android/1.0")

            val status = connection.responseCode
            if (status !in 200..299) {
                throw RouteRequestException("OSRM respondió con código $status")
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val firstRoute = json.decodeFromString<OsrmRouteResponse>(response).routes.firstOrNull()
                ?: throw RouteRequestException("OSRM no encontró una ruta disponible")
            val points = firstRoute.geometry.coordinates.mapNotNull { coordinate ->
                if (coordinate.size < 2) null else LatLng(coordinate[1], coordinate[0])
            }
            if (points.size < 2) {
                throw RouteRequestException("La ruta recibida no contiene suficientes puntos")
            }

            MapRoute(
                points = points,
                distanceMeters = firstRoute.distance,
                durationSeconds = firstRoute.duration,
            )
        } finally {
            connection.disconnect()
        }
    }
}

class RouteRequestException(message: String) : Exception(message)
