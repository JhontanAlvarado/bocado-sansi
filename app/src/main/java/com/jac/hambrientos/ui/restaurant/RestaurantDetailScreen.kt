package com.jac.hambrientos.ui.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jac.hambrientos.ui.map.demoMapRestaurants
import com.jac.hambrientos.ui.theme.AccentGreen
import com.jac.hambrientos.ui.theme.AccentYellow
import com.jac.hambrientos.ui.theme.HambrientosTheme
import com.jac.hambrientos.ui.theme.OutlineWarm
import com.jac.hambrientos.ui.theme.PrimaryDark
import com.jac.hambrientos.ui.theme.PrimaryOrange
import com.jac.hambrientos.ui.theme.SoftGreen
import com.jac.hambrientos.ui.theme.SoftOrange
import com.jac.hambrientos.ui.theme.TextPrimary
import com.jac.hambrientos.ui.theme.TextSecondary
import com.jac.hambrientos.ui.theme.WarmSurface

private data class RestaurantUiModel(
    val name: String,
    val category: String,
    val rating: String,
    val reviewCount: Int,
    val price: String,
    val distance: String,
    val schedule: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
)

private data class RatingItem(val label: String, val value: Float)

private val ratingItems = listOf(
    RatingItem("Sabor", 4.8f),
    RatingItem("Precio", 4.6f),
    RatingItem("Higiene", 4.5f),
    RatingItem("Atención", 4.4f),
)

private fun restaurantFor(restaurantId: String): RestaurantUiModel = when (restaurantId) {
    "2", "burger-lab" -> restaurantUiModel("2", "Burger Lab", "Comida casera", "4.3", 96, "Bs 15–25", "193 m", "11:30–22:30", "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1200&q=85")
    "3", "dona-meche" -> restaurantUiModel("3", "Doña Meche", "Comida casera", "4.5", 91, "Bs 12–20", "160 m", "10:30–20:30", "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1200&q=85")
    "4", "pizza-patio" -> restaurantUiModel("4", "Pizza Patio", "Pizza", "4.4", 73, "Bs 18–32", "230 m", "12:00–22:30", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=1200&q=85")
    "5", "cafe-central" -> restaurantUiModel("5", "Café Central", "Cafetería", "4.6", 82, "Bs 8–22", "80 m", "07:30–20:00", "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=1200&q=85")
    "6", "pollos-15" -> restaurantUiModel("6", "Pollos 15", "Pollo y combos", "4.5", 104, "Bs 15–30", "140 m", "11:00–22:00", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?auto=format&fit=crop&w=1200&q=85")
    "7", "verde-menta" -> restaurantUiModel("7", "Verde Menta", "Comida saludable", "4.7", 68, "Bs 18–32", "210 m", "09:00–20:00", "https://images.unsplash.com/photo-1543362906-acfc16c67564?auto=format&fit=crop&w=1200&q=85")
    "8", "la-caserita" -> restaurantUiModel("8", "La Caserita", "Comida casera", "4.4", 119, "Bs 12–24", "260 m", "10:30–21:00", "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1200&q=85")
    "9", "sazon-umss" -> restaurantUiModel("9", "Sazón UMSS", "Menú del día", "4.3", 77, "Bs 10–20", "320 m", "10:00–18:00", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=1200&q=85")
    else -> restaurantUiModel("1", "La Esquina Sansi", "Hamburguesas", "4.2", 128, "Bs 10–18", "120 m", "11:00–22:00", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=1200&q=85")
}

private fun restaurantUiModel(
    mapRestaurantId: String,
    name: String,
    category: String,
    rating: String,
    reviewCount: Int,
    price: String,
    distance: String,
    schedule: String,
    imageUrl: String,
): RestaurantUiModel {
    val position = demoMapRestaurants.firstOrNull { it.id == mapRestaurantId }?.position
    return RestaurantUiModel(
        name = name,
        category = category,
        rating = rating,
        reviewCount = reviewCount,
        price = price,
        distance = distance,
        schedule = schedule,
        imageUrl = imageUrl,
        latitude = position?.latitude ?: -17.3935,
        longitude = position?.longitude ?: -66.1455,
    )
}

@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDirectionsClick: (() -> Unit)? = null,
    onRateClick: () -> Unit,
) {
    val restaurant = restaurantFor(restaurantId)
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmSurface,
        bottomBar = {
            RestaurantBottomBar(
                onDirectionsClick = {
                    onDirectionsClick?.invoke() ?: openDirections(
                        context = context,
                        latitude = restaurant.latitude,
                        longitude = restaurant.longitude,
                        restaurantName = restaurant.name,
                    )
                },
                onRateClick = onRateClick,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 30.dp),
        ) {
            item {
                HeroSection(restaurant = restaurant, onBackClick = onBackClick)
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(22.dp))
                    RestaurantHeading(restaurant)
                    Spacer(Modifier.height(24.dp))
                    QuickInfoRow(restaurant)
                    Spacer(Modifier.height(32.dp))
                    SectionTitle("Calificaciones")
                    Spacer(Modifier.height(16.dp))
                    RatingsCard()
                    Spacer(Modifier.height(32.dp))
                    SectionTitle("Sobre el lugar")
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Comida rica, porciones generosas y precios pensados para estudiantes. A pocos minutos del campus.",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                    )
                    Spacer(Modifier.height(32.dp))
                    SectionTitle("Menú y precios")
                    Spacer(Modifier.height(14.dp))
                    MenuCard()
                    Spacer(Modifier.height(32.dp))
                    SectionTitle("¿Qué dicen los estudiantes?")
                    Spacer(Modifier.height(14.dp))
                    FeaturedQuote()
                    Spacer(Modifier.height(14.dp))
                    StudentReviewCard()
                    Spacer(Modifier.height(20.dp))
                    TextButton(
                        onClick = {},
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        Icon(Icons.Default.Flag, null, tint = TextSecondary, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(7.dp))
                        Text("Reportar información", color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSection(restaurant: RestaurantUiModel, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(SoftOrange),
    ) {
        AsyncImage(
            model = restaurant.imageUrl,
            contentDescription = "Foto de ${restaurant.name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeroIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Volver", onBackClick)
            Spacer(Modifier.weight(1f))
            HeroIconButton(Icons.Default.FavoriteBorder, "Agregar a favoritos", {})
            Spacer(Modifier.width(10.dp))
            HeroIconButton(Icons.Default.Share, "Compartir", {})
        }
    }
}

@Composable
private fun HeroIconButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = WarmSurface.copy(alpha = 0.94f),
        shadowElevation = 4.dp,
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, description, tint = TextPrimary, modifier = Modifier.size(21.dp))
        }
    }
}

@Composable
private fun RestaurantHeading(restaurant: RestaurantUiModel) {
    Surface(shape = RoundedCornerShape(50), color = SoftOrange) {
        Text(
            text = restaurant.category,
            color = PrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
    Spacer(Modifier.height(13.dp))
    Text(
        text = restaurant.name,
        color = TextPrimary,
        fontWeight = FontWeight.Black,
        fontSize = 29.sp,
        lineHeight = 35.sp,
        letterSpacing = (-0.5).sp,
    )
    Spacer(Modifier.height(9.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, null, tint = AccentGreen, modifier = Modifier.size(17.dp))
        Spacer(Modifier.width(6.dp))
        Text("Información verificada", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
    Spacer(Modifier.height(18.dp))
    Row(verticalAlignment = Alignment.Bottom) {
        Text(restaurant.rating, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 34.sp)
        Icon(
            Icons.Default.Star,
            null,
            tint = PrimaryOrange,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp).size(20.dp),
        )
        Text(
            text = "${restaurant.reviewCount} reseñas",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 10.dp, bottom = 8.dp),
        )
    }
}

@Composable
private fun QuickInfoRow(restaurant: RestaurantUiModel) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickInfoCard("Precio", restaurant.price, "Bs", Modifier.weight(1f))
        QuickInfoCard("Distancia", restaurant.distance, "⌖", Modifier.weight(1f))
        QuickInfoCard("Horario", restaurant.schedule, "◷", Modifier.weight(1f))
    }
}

@Composable
private fun QuickInfoCard(label: String, value: String, symbol: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(112.dp),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Surface(modifier = Modifier.size(29.dp), shape = CircleShape, color = SoftOrange) {
                Box(contentAlignment = Alignment.Center) {
                    Text(symbol, color = PrimaryDark, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
            Text(label, color = TextSecondary, fontSize = 11.sp)
            Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun RatingsCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            ratingItems.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.label, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.width(66.dp))
                    LinearProgressIndicator(
                        progress = { item.value / 5f },
                        modifier = Modifier.weight(1f).height(7.dp).clip(CircleShape),
                        color = PrimaryOrange,
                        trackColor = OutlineWarm,
                    )
                    Text(item.value.toString(), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(start = 12.dp))
                }
                if (index != ratingItems.lastIndex) Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MenuCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            MenuItem("Hamburguesa Sansi", "Bs 18")
            HorizontalDivider(color = OutlineWarm)
            MenuItem("Combo estudiantil", "Bs 22")
        }
    }
}

@Composable
private fun MenuItem(name: String, price: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(price, color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun FeaturedQuote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = AccentGreen,
        shadowElevation = 3.dp,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("“", color = AccentYellow, fontWeight = FontWeight.Black, fontSize = 38.sp, lineHeight = 28.sp)
            Text(
                text = "Todo fresco y el precio está muy bien para estudiantes.",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                lineHeight = 23.sp,
            )
        }
    }
}

@Composable
private fun StudentReviewCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = SoftGreen) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("JM", color = AccentGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("José M.", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Estudiante verificado", color = AccentGreen, fontSize = 11.sp)
                }
                Row {
                    repeat(5) {
                        Icon(Icons.Default.Star, null, tint = PrimaryOrange, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(Modifier.height(15.dp))
            Text(
                text = "Las porciones son buenas y atienden rápido. Volvería por el combo estudiantil.",
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text("Hace 3 días", color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = (-0.2).sp)
}

@Composable
private fun RestaurantBottomBar(onDirectionsClick: () -> Unit, onRateClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = WarmSurface, shadowElevation = 12.dp) {
        Row(
            modifier = Modifier.navigationBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onDirectionsClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryOrange),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDark),
            ) {
                Icon(Icons.AutoMirrored.Filled.DirectionsWalk, null, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(7.dp))
                Text("Cómo llegar", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onRateClick,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            ) {
                Icon(Icons.Default.Star, null, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(7.dp))
                Text("Calificar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFDF9, widthDp = 390, heightDp = 844)
@Composable
private fun RestaurantDetailPreview() {
    HambrientosTheme(darkTheme = false, dynamicColor = false) {
        RestaurantDetailScreen(
            restaurantId = "1",
            onBackClick = {},
            onRateClick = {},
        )
    }
}
