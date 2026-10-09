package com.jac.hambrientos.ui.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
import com.jac.hambrientos.ui.theme.WarmBackground
import com.jac.hambrientos.ui.theme.WarmSurface

private const val MaxReviewLength = 500

private data class RateRestaurantUi(
    val name: String,
    val category: String,
    val distance: String,
    val imageUrl: String,
)

private fun restaurantFor(restaurantId: String): RateRestaurantUi = when (restaurantId) {
    "2" -> RateRestaurantUi("Burger Lab", "Comida casera", "193 m", "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=500&q=80")
    "3" -> RateRestaurantUi("Doña Meche", "Comida casera", "160 m", "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=500&q=80")
    "4" -> RateRestaurantUi("Pizza Patio", "Pizza", "230 m", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=500&q=80")
    "5" -> RateRestaurantUi("Café Central", "Cafetería", "80 m", "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=500&q=80")
    "6" -> RateRestaurantUi("Pollos 15", "Pollo y combos", "140 m", "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?auto=format&fit=crop&w=500&q=80")
    "7" -> RateRestaurantUi("Verde Menta", "Comida saludable", "210 m", "https://images.unsplash.com/photo-1543362906-acfc16c67564?auto=format&fit=crop&w=500&q=80")
    "8" -> RateRestaurantUi("La Caserita", "Comida casera", "260 m", "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=500&q=80")
    "9" -> RateRestaurantUi("Sazón UMSS", "Menú del día", "320 m", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=500&q=80")
    else -> RateRestaurantUi("La Esquina Sansi", "Hamburguesas", "120 m", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=500&q=80")
}

@Composable
fun RateScreen(
    restaurantId: String,
    onBackClick: () -> Unit,
    onReturnToRestaurant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val restaurant = restaurantFor(restaurantId)
    var flavorRating by rememberSaveable { mutableIntStateOf(5) }
    var priceRating by rememberSaveable { mutableIntStateOf(4) }
    var hygieneRating by rememberSaveable { mutableIntStateOf(0) }
    var serviceRating by rememberSaveable { mutableIntStateOf(0) }
    var review by rememberSaveable { mutableStateOf("") }
    var isPublished by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = { RateTopBar(onBackClick = onBackClick) },
    ) { innerPadding ->
        if (isPublished) {
            SuccessContent(
                onReturnToRestaurant = onReturnToRestaurant,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    top = 18.dp,
                    end = 20.dp,
                    bottom = 32.dp,
                ),
            ) {
                item {
                    RestaurantSummaryCard(restaurant)
                    Spacer(Modifier.height(30.dp))
                    Text(
                        text = "¿Cómo fue tu experiencia?",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        lineHeight = 33.sp,
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = "Tu opinión ayuda a otros estudiantes a decidir mejor.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )
                    Spacer(Modifier.height(25.dp))
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            StarRatingRow("Sabor", flavorRating) { flavorRating = it }
                            StarRatingRow("Precio", priceRating) { priceRating = it }
                            StarRatingRow("Higiene", hygieneRating) { hygieneRating = it }
                            StarRatingRow("Atención", serviceRating) { serviceRating = it }
                        }
                    }
                    Spacer(Modifier.height(30.dp))
                    Text(
                        text = "Tu reseña",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp,
                    )
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(
                        value = review,
                        onValueChange = { value ->
                            if (value.length <= MaxReviewLength) review = value
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = {
                            Text(
                                text = "Cuéntale a otros estudiantes cómo fue tu experiencia…",
                                color = TextSecondary.copy(alpha = 0.75f),
                            )
                        },
                        supportingText = {
                            Text(
                                text = "${review.length}/$MaxReviewLength",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = TextSecondary,
                            )
                        },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WarmSurface,
                            unfocusedContainerColor = WarmSurface,
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = OutlineWarm,
                        ),
                    )
                    Spacer(Modifier.height(18.dp))
                    VerificationNotice()
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { isPublished = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    ) {
                        Text("Publicar reseña", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RateTopBar(onBackClick: () -> Unit) {
    Surface(color = WarmSurface, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(62.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextPrimary,
                )
            }
            Text(
                text = "Calificar",
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

@Composable
private fun RestaurantSummaryCard(restaurant: RateRestaurantUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = restaurant.imageUrl,
                contentDescription = "Foto de ${restaurant.name}",
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftOrange),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = restaurant.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(restaurant.category, color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(9.dp))
                Text(restaurant.distance, color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StarRatingRow(label: String, rating: Int, onRatingChanged: (Int) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (rating == 0) "Sin calificar" else "$rating/5",
                color = if (rating == 0) TextSecondary else PrimaryDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            )
        }
        Row(modifier = Modifier.padding(top = 5.dp)) {
            repeat(5) { index ->
                val star = index + 1
                IconButton(
                    onClick = { onRatingChanged(star) },
                    modifier = Modifier.size(42.dp),
                ) {
                    Icon(
                        imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.Star,
                        contentDescription = "$star estrellas para $label",
                        tint = if (star <= rating) PrimaryOrange else OutlineWarm,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SoftGreen,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Solo puedes dejar una reseña por local.",
                color = AccentGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun SuccessContent(
    onReturnToRestaurant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier.size(112.dp),
            shape = CircleShape,
            color = SoftGreen,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(62.dp),
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = "¡Reseña publicada!",
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(11.dp))
        Text(
            text = "Gracias por ayudar a la comunidad Sansi a comer mejor.",
            color = TextSecondary,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(23.dp))
        Surface(shape = RoundedCornerShape(50), color = AccentYellow.copy(alpha = 0.42f)) {
            Text(
                text = "+20 puntos de comunidad",
                color = PrimaryDark,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 17.dp, vertical = 9.dp),
            )
        }
        Spacer(Modifier.height(34.dp))
        Button(
            onClick = onReturnToRestaurant,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
        ) {
            Text("Volver al local", fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFAF4, widthDp = 390, heightDp = 844)
@Composable
private fun RateScreenPreview() {
    HambrientosTheme(darkTheme = false, dynamicColor = false) {
        RateScreen(
            restaurantId = "1",
            onBackClick = {},
            onReturnToRestaurant = {},
        )
    }
}
