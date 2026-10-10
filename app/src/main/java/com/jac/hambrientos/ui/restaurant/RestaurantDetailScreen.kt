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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jac.hambrientos.data.local.RestaurantProduct
import com.jac.hambrientos.data.local.RestaurantPromotion
import com.jac.hambrientos.data.local.productsForRestaurant
import com.jac.hambrientos.data.local.promotionsForRestaurant
import com.jac.hambrientos.data.local.restaurantById
import com.jac.hambrientos.ui.cart.AddToCartResult
import com.jac.hambrientos.ui.cart.CartBadgeButton
import com.jac.hambrientos.ui.cart.CartState
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
    val description: String,
)

private data class RatingItem(val label: String, val value: Float)

private val ratingItems = listOf(
    RatingItem("Sabor", 4.8f),
    RatingItem("Precio", 4.6f),
    RatingItem("Higiene", 4.5f),
    RatingItem("Atención", 4.4f),
)

private fun restaurantFor(restaurantId: Int): RestaurantUiModel {
    val restaurant = restaurantById(restaurantId) ?: restaurantById(1)!!
    return RestaurantUiModel(
        name = restaurant.name,
        category = restaurant.category,
        rating = restaurant.rating.toString(),
        reviewCount = 60 + restaurant.id * 11,
        price = restaurant.priceRange,
        distance = "${restaurant.distanceMeters} m",
        schedule = restaurant.hours,
        imageUrl = restaurant.imageUrl,
        description = restaurant.description,
    )
}

@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDirectionsClick: () -> Unit,
    onRateClick: () -> Unit,
    onCartClick: () -> Unit,
    cartState: CartState,
) {
    val numericRestaurantId = restaurantId.toIntOrNull()
        ?.takeIf { restaurantById(it) != null }
        ?: 1
    val restaurant = restaurantFor(numericRestaurantId)
    val products = remember(numericRestaurantId) { productsForRestaurant(numericRestaurantId) }
    val promotions = remember(numericRestaurantId) { promotionsForRestaurant(numericRestaurantId) }
    var pendingProduct by remember { mutableStateOf<RestaurantProduct?>(null) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmSurface,
        bottomBar = {
            RestaurantBottomBar(
                onDirectionsClick = {
                    onDirectionsClick()
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
                HeroSection(
                    restaurant = restaurant,
                    onBackClick = onBackClick,
                    cartItemCount = cartState.itemCount,
                    onCartClick = onCartClick,
                )
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
                        text = restaurant.description,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                    )
                    if (promotions.isNotEmpty()) {
                        Spacer(Modifier.height(32.dp))
                        SectionTitle("Promos de hoy")
                        Spacer(Modifier.height(14.dp))
                        promotions.forEach { promotion ->
                            DetailPromotionCard(promotion)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                    if (products.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        SectionTitle("Menú")
                        Spacer(Modifier.height(14.dp))
                        products.forEach { product ->
                            ProductCard(
                                product = product,
                                onAdd = {
                                    when (cartState.addProduct(product)) {
                                        AddToCartResult.Added -> Unit
                                        AddToCartResult.DifferentRestaurant -> pendingProduct = product
                                    }
                                },
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
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

    pendingProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { pendingProduct = null },
            title = { Text("Ya tienes productos de otro local") },
            text = {
                Text("Para agregar ${product.name}, primero debemos vaciar el carrito actual.")
            },
            dismissButton = {
                TextButton(onClick = { pendingProduct = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        cartState.clear()
                        cartState.addProduct(product)
                        pendingProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                ) {
                    Text("Vaciar carrito y agregar")
                }
            },
        )
    }
}

@Composable
private fun HeroSection(
    restaurant: RestaurantUiModel,
    onBackClick: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit,
) {
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
            Surface(
                shape = CircleShape,
                color = WarmSurface.copy(alpha = 0.94f),
                shadowElevation = 4.dp,
            ) {
                CartBadgeButton(
                    itemCount = cartItemCount,
                    onClick = onCartClick,
                    iconTint = TextPrimary,
                )
            }
            Spacer(Modifier.width(10.dp))
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
private fun DetailPromotionCard(promotion: RestaurantPromotion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AccentYellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    promotion.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                )
                Surface(shape = RoundedCornerShape(50), color = AccentGreen) {
                    Text(
                        "HOY",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(promotion.description, color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                promotion.originalPrice?.let {
                    Text(
                        "Bs ${it.toInt()}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.LineThrough,
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    "Bs ${promotion.promoPrice.toInt()}",
                    color = PrimaryDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp,
                )
            }
        }
    }
}

@Composable
private fun ProductCard(product: RestaurantProduct, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(SoftOrange),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(3.dp))
                Text(
                    product.description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    maxLines = 2,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Bs ${product.price.toInt()}",
                        color = PrimaryDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = onAdd,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text("Agregar", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
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
            onDirectionsClick = {},
            onRateClick = {},
            onCartClick = {},
            cartState = remember { CartState() },
        )
    }
}
