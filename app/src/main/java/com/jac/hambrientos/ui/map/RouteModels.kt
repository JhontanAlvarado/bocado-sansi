package com.jac.hambrientos.ui.map

import kotlinx.serialization.Serializable
import org.maplibre.android.geometry.LatLng

@Serializable
data class OsrmRouteResponse(
    val routes: List<OsrmRoute> = emptyList(),
)

@Serializable
data class OsrmRoute(
    val geometry: OsrmGeometry,
    val distance: Double,
    val duration: Double,
)

@Serializable
data class OsrmGeometry(
    val coordinates: List<List<Double>>,
)

data class MapRoute(
    val points: List<LatLng>,
    val distanceMeters: Double,
    val durationSeconds: Double,
)

sealed interface RouteUiState {
    data object Idle : RouteUiState
    data object RequestingPermission : RouteUiState
    data object Locating : RouteUiState
    data object Calculating : RouteUiState
    data object PermissionDenied : RouteUiState
    data object LocationUnavailable : RouteUiState
    data class Loaded(val route: MapRoute) : RouteUiState
    data class Error(val message: String) : RouteUiState
}
