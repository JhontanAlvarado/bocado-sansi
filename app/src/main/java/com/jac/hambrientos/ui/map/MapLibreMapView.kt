@file:Suppress("DEPRECATION")

package com.jac.hambrientos.ui.map

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Icon
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory.lineCap
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineJoin
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.tile.TileOperation
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

private const val MapStyleUrl = "https://tiles.openfreemap.org/styles/liberty"
private const val MapLogTag = "HambrientosMap"
private const val InitialZoom = 15.7
private const val RouteSourceId = "active-route-source"
private const val RouteLayerId = "active-route-layer"
private val UmssCampus = LatLng(-17.3935, -66.1455)

@Composable
fun MapLibreMapView(
    restaurants: List<MapRestaurant>,
    currentLocation: LatLng?,
    destination: MapRestaurant?,
    routePoints: List<LatLng>,
    onRestaurantSelected: (MapRestaurant) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentRestaurants by rememberUpdatedState(restaurants)
    val currentOnRestaurantSelected by rememberUpdatedState(onRestaurantSelected)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val restaurantIdsByMarker = remember { mutableMapOf<Long, String>() }
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var styleLoaded by remember { mutableStateOf(false) }

    val mapView = remember(context) {
        MapLibre.getInstance(context.applicationContext)
        MapView(context).also { it.onCreate(null) }
    }

    LaunchedEffect(mapView) {
        mapView.addOnDidFailLoadingMapListener { errorMessage ->
            Log.e(MapLogTag, "Style or source loading failed: $errorMessage")
        }
        mapView.addOnTileActionListener { operation, x, y, z, wrap, overscaledZ, sourceId ->
            if (operation == TileOperation.Error) {
                Log.e(
                    MapLogTag,
                    "Tile loading failed: source=$sourceId z=$z x=$x y=$y " +
                        "wrap=$wrap overscaledZ=$overscaledZ",
                )
            }
        }
        mapView.getMapAsync { mapLibreMap ->
            map = mapLibreMap
            mapLibreMap.cameraPosition = CameraPosition.Builder()
                .target(UmssCampus)
                .zoom(InitialZoom)
                .build()
            mapLibreMap.uiSettings.apply {
                isCompassEnabled = false
            }
            mapLibreMap.addOnMapClickListener {
                currentOnMapClick()
                false
            }
            mapLibreMap.setOnMarkerClickListener { marker ->
                restaurantIdsByMarker[marker.id]
                    ?.let { id -> currentRestaurants.firstOrNull { it.id == id } }
                    ?.also(currentOnRestaurantSelected)
                true
            }
            mapLibreMap.setStyle(MapStyleUrl) {
                styleLoaded = true
            }
        }
    }

    LaunchedEffect(map, styleLoaded, restaurants, currentLocation, destination) {
        val mapLibreMap = map ?: return@LaunchedEffect
        if (!styleLoaded) return@LaunchedEffect

        mapLibreMap.removeAnnotations()
        restaurantIdsByMarker.clear()

        mapLibreMap.addMarker(
            MarkerOptions()
                .position(UmssCampus)
                .title("UMSS - Campus Central")
                .snippet("Campus universitario")
                .icon(createMarkerIcon(context, Color.rgb(30, 105, 190), 42)),
        )

        val restaurantIcon = createMarkerIcon(context, Color.rgb(241, 112, 41), 36)
        val destinationIcon = createMarkerIcon(context, Color.rgb(211, 55, 48), 44)
        restaurants.forEach { restaurant ->
            val marker = mapLibreMap.addMarker(
                MarkerOptions()
                    .position(restaurant.position)
                    .title(restaurant.name)
                    .snippet("${restaurant.category} · ${restaurant.price}")
                    .icon(if (restaurant.id == destination?.id) destinationIcon else restaurantIcon),
            )
            restaurantIdsByMarker[marker.id] = restaurant.id
        }

        currentLocation?.let { location ->
            mapLibreMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Tu ubicación")
                    .icon(createMarkerIcon(context, Color.rgb(35, 112, 214), 40)),
            )
        }

        if (destination != null && restaurants.none { it.id == destination.id }) {
            mapLibreMap.addMarker(
                MarkerOptions()
                    .position(destination.position)
                    .title(destination.name)
                    .snippet("Destino")
                    .icon(destinationIcon),
            )
        }
    }

    LaunchedEffect(map, styleLoaded, routePoints) {
        val mapLibreMap = map ?: return@LaunchedEffect
        if (!styleLoaded) return@LaunchedEffect
        mapLibreMap.getStyle { style ->
            style.removeLayer(RouteLayerId)
            style.removeSource(RouteSourceId)

            if (routePoints.size >= 2) {
                val lineString = LineString.fromLngLats(
                    routePoints.map { Point.fromLngLat(it.longitude, it.latitude) },
                )
                style.addSource(GeoJsonSource(RouteSourceId, lineString))
                style.addLayer(
                    LineLayer(RouteLayerId, RouteSourceId).withProperties(
                        lineColor("#E95B22"),
                        lineWidth(6f),
                        lineCap(Property.LINE_CAP_ROUND),
                        lineJoin(Property.LINE_JOIN_ROUND),
                    ),
                )
            }
        }
    }

    LaunchedEffect(map, routePoints, currentLocation, destination) {
        val mapLibreMap = map ?: return@LaunchedEffect
        if (routePoints.size < 2 || currentLocation == null || destination == null) {
            return@LaunchedEffect
        }
        val bounds = LatLngBounds.Builder()
            .includes(routePoints + currentLocation + destination.position)
            .build()
        val density = context.resources.displayMetrics.density
        val sidePadding = (48 * density).toInt()
        val bottomPadding = (220 * density).toInt()
        mapView.post {
            mapLibreMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    sidePadding,
                    sidePadding,
                    sidePadding,
                    bottomPadding,
                ),
                650,
            )
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        var destroyed = false
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> {
                    mapView.onDestroy()
                    destroyed = true
                }
                else -> Unit
            }
        }
        val memoryCallbacks = object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) = Unit
            override fun onLowMemory() = mapView.onLowMemory()
            override fun onTrimMemory(level: Int) {
                if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) mapView.onLowMemory()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        context.applicationContext.registerComponentCallbacks(memoryCallbacks)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            context.applicationContext.unregisterComponentCallbacks(memoryCallbacks)
            if (!destroyed) {
                mapView.onPause()
                mapView.onStop()
                mapView.onDestroy()
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}

private fun createMarkerIcon(context: Context, color: Int, sizeDp: Int): Icon {
    val density = context.resources.displayMetrics.density
    val width = (sizeDp * density).toInt()
    val height = (sizeDp * 1.25f * density).toInt()
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val centerX = width / 2f
    val radius = width * 0.34f
    val centerY = radius + width * 0.11f
    val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        this.color = Color.WHITE
    }
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        this.color = color
    }
    val tail = Path().apply {
        moveTo(centerX - radius * 0.52f, centerY + radius * 0.62f)
        lineTo(centerX, height.toFloat())
        lineTo(centerX + radius * 0.52f, centerY + radius * 0.62f)
        close()
    }

    canvas.drawPath(tail, border)
    canvas.drawCircle(centerX, centerY, radius + density * 2f, border)
    canvas.drawPath(tail, fill)
    canvas.drawCircle(centerX, centerY, radius, fill)
    canvas.drawCircle(centerX, centerY, radius * 0.34f, border)

    return IconFactory.getInstance(context).fromBitmap(bitmap)
}
