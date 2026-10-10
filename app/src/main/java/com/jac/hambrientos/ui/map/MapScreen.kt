package com.jac.hambrientos.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.core.content.ContextCompat
import com.jac.hambrientos.ui.home.BottomNavigationBar
import com.jac.hambrientos.ui.theme.AccentGreen
import com.jac.hambrientos.ui.theme.OutlineWarm
import com.jac.hambrientos.ui.theme.PrimaryDark
import com.jac.hambrientos.ui.theme.PrimaryOrange
import com.jac.hambrientos.ui.theme.SoftOrange
import com.jac.hambrientos.ui.theme.TextPrimary
import com.jac.hambrientos.ui.theme.TextSecondary
import com.jac.hambrientos.ui.theme.WarmBackground
import com.jac.hambrientos.ui.theme.WarmSurface
import org.maplibre.android.geometry.LatLng
import kotlin.math.ceil

private const val UnderBs15Filter = "< Bs15"

@Composable
fun MapScreen(
    destinationRestaurantId: String? = null,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val routeService = remember { OsrmRouteService() }
    var selectedFilter by rememberSaveable { mutableStateOf("Todos") }
    var selectedRestaurant by remember { mutableStateOf<MapRestaurant?>(null) }
    var activeDestinationId by rememberSaveable(destinationRestaurantId) {
        mutableStateOf(destinationRestaurantId)
    }
    val destination = remember(activeDestinationId) {
        demoMapRestaurants.firstOrNull { it.id == activeDestinationId }
    }
    var currentLocation by remember(activeDestinationId) { mutableStateOf<LatLng?>(null) }
    var routeState by remember(activeDestinationId) {
        mutableStateOf<RouteUiState>(RouteUiState.Idle)
    }
    var locationRequestNonce by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            routeState = RouteUiState.Locating
            locationRequestNonce++
        } else {
            routeState = RouteUiState.PermissionDenied
        }
    }

    fun startLocationFlow() {
        if (hasLocationPermission(context)) {
            routeState = RouteUiState.Locating
            locationRequestNonce++
        } else {
            routeState = RouteUiState.RequestingPermission
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    LaunchedEffect(destination?.id) {
        if (destination == null) {
            currentLocation = null
            routeState = RouteUiState.Idle
        } else {
            startLocationFlow()
        }
    }

    DisposableEffect(locationRequestNonce, destination?.id) {
        if (locationRequestNonce == 0 || destination == null) {
            onDispose { }
        } else {
            routeState = RouteUiState.Locating
            var disposed = false
            val cancellation = requestCurrentLocation(
                context = context,
                onLocation = { location ->
                    if (!disposed) {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        routeState = RouteUiState.Calculating
                    }
                },
                onUnavailable = {
                    if (!disposed) routeState = RouteUiState.LocationUnavailable
                },
                onError = {
                    if (!disposed) routeState = RouteUiState.LocationUnavailable
                },
            )
            onDispose {
                disposed = true
                cancellation.cancel()
            }
        }
    }

    LaunchedEffect(currentLocation, destination?.id) {
        val origin = currentLocation ?: return@LaunchedEffect
        val routeDestination = destination ?: return@LaunchedEffect
        routeState = RouteUiState.Calculating
        routeState = try {
            RouteUiState.Loaded(routeService.route(origin, routeDestination.position))
        } catch (error: Exception) {
            RouteUiState.Error(
                error.message ?: "No pudimos calcular la ruta. Intenta nuevamente.",
            )
        }
    }
    val visibleRestaurants = remember(selectedFilter) {
        if (selectedFilter == UnderBs15Filter) {
            demoMapRestaurants.filter(MapRestaurant::isUnderBs15)
        } else {
            demoMapRestaurants
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = "map",
                onHomeClick = onHomeClick,
                onMapClick = {},
                onProfileClick = onProfileClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            MapLibreMapView(
                modifier = Modifier.fillMaxSize(),
                restaurants = visibleRestaurants,
                currentLocation = currentLocation,
                destination = destination,
                routePoints = (routeState as? RouteUiState.Loaded)?.route?.points.orEmpty(),
                onRestaurantSelected = { selectedRestaurant = it },
                onMapClick = { selectedRestaurant = null },
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 12.dp),
            ) {
                MapSearchBar(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(11.dp))
                MapFilterChips(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        if (filter == UnderBs15Filter && selectedRestaurant?.isUnderBs15 == false) {
                            selectedRestaurant = null
                        }
                    },
                )
            }

            if (destination != null) {
                RouteStatusCard(
                    destination = destination,
                    state = routeState,
                    onRetry = {
                        currentLocation = null
                        startLocationFlow()
                    },
                    onCancel = {
                        activeDestinationId = null
                        currentLocation = null
                        routeState = RouteUiState.Idle
                        selectedRestaurant = null
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                )
            } else {
                selectedRestaurant?.let { restaurant ->
                    MapRestaurantCard(
                        restaurant = restaurant,
                        onViewRestaurant = { onRestaurantClick(restaurant.id) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteStatusCard(
    destination: MapRestaurant,
    state: RouteUiState,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = SoftOrange) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.padding(8.dp).size(20.dp),
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Cómo llegar a ${destination.name}",
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))

            when (state) {
                RouteUiState.Idle,
                RouteUiState.RequestingPermission -> RouteMessage("Solicitando permiso de ubicación…")
                RouteUiState.Locating -> RouteMessage("Buscando tu ubicación actual…")
                RouteUiState.Calculating -> RouteMessage("Calculando la mejor ruta disponible…")
                RouteUiState.PermissionDenied -> RouteMessage(
                    "Necesitamos permiso de ubicación para calcular cómo llegar.",
                )
                RouteUiState.LocationUnavailable -> RouteMessage(
                    "No pudimos obtener tu ubicación. Activa la ubicación e intenta otra vez.",
                )
                is RouteUiState.Error -> RouteMessage(
                    "No pudimos obtener la ruta. ${state.message}",
                )
                is RouteUiState.Loaded -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        RouteMetric("Distancia", formatDistance(state.route.distanceMeters))
                        RouteMetric("Tiempo estimado", formatDuration(state.route.durationSeconds))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Referencia vehicular: el servidor público de OSRM no ofrece perfil peatonal.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (state is RouteUiState.PermissionDenied ||
                    state is RouteUiState.LocationUnavailable ||
                    state is RouteUiState.Error
                ) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    ) {
                        Text("Reintentar", fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                ) {
                    Text("Cancelar ruta", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RouteMessage(message: String) {
    Text(message, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
}

@Composable
private fun RouteMetric(label: String, value: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

private fun formatDistance(meters: Double): String =
    if (meters < 1_000) "${meters.toInt()} m" else "%.1f km".format(meters / 1_000)

private fun formatDuration(seconds: Double): String =
    "${ceil(seconds / 60).toInt().coerceAtLeast(1)} min"

@Composable
private fun MapSearchBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        color = WarmSurface,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
    ) {
        Row(
            modifier = Modifier.padding(start = 17.dp, top = 10.dp, end = 9.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Search, "Buscar", tint = TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(11.dp))
            Text(
                text = "Buscar en el mapa",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
            )
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(13.dp), color = SoftOrange) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Tune, "Filtros", tint = PrimaryDark, modifier = Modifier.size(21.dp))
                }
            }
        }
    }
}

@Composable
fun MapFilterChips(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filters = listOf("Todos", "Cerca", UnderBs15Filter, "Bs15–25", "⭐ 4+")
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(filters) { filter ->
            val selected = filter == selectedFilter
            Surface(
                modifier = Modifier.clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(50),
                color = if (selected) PrimaryOrange else WarmSurface,
                shadowElevation = if (selected) 4.dp else 2.dp,
                border = if (selected) null else BorderStroke(1.dp, OutlineWarm),
            ) {
                Text(
                    text = filter,
                    color = if (selected) Color.White else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                )
            }
        }
    }
}

@Composable
fun MapRestaurantCard(
    restaurant: MapRestaurant,
    onViewRestaurant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = "Foto de ${restaurant.name}",
                modifier = Modifier
                    .size(width = 94.dp, height = 108.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(SoftOrange),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = restaurant.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(Icons.Default.Star, null, tint = PrimaryOrange, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(restaurant.rating.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(Modifier.height(3.dp))
                Text(restaurant.category, color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(restaurant.price, color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Default.LocationOn, null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                    Text(restaurant.distance, color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
                Spacer(Modifier.height(11.dp))
                Button(
                    onClick = onViewRestaurant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(39.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Text("Ver local", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
