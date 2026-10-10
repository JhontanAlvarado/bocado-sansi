package com.jac.hambrientos.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jac.hambrientos.data.local.RestaurantPromotion
import com.jac.hambrientos.data.local.Restaurant
import com.jac.hambrientos.data.local.demoPromotions
import com.jac.hambrientos.data.local.demoProducts
import com.jac.hambrientos.data.local.demoRestaurants
import com.jac.hambrientos.data.local.restaurantNameForId
import com.jac.hambrientos.ui.cart.CartBadgeButton
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
import java.text.Normalizer
import java.util.Locale

private data class Category(
    val name: String,
    val emoji: String,
    val background: Color,
)

private val categories = listOf(
    Category("Hamburguesas", "🍔", Color(0xFFFFE4D6)),
    Category("Comida casera", "🍲", Color(0xFFFFEECF)),
    Category("Pollo", "🍗", Color(0xFFFFE2C2)),
    Category("Pizza", "🍕", Color(0xFFFFDCD6)),
    Category("Cafeterías", "☕", Color(0xFFECE3D5)),
    Category("Saludable", "🥗", Color(0xFFDDF0E4)),
    Category("Postres", "🍰", Color(0xFFFFE1E8)),
    Category("Comida rápida", "🌭", Color(0xFFFFE9BF)),
)

internal fun filterHomeRestaurants(
    selectedCategory: String?,
    searchQuery: String,
): List<Restaurant> {
    val normalizedQuery = searchQuery.normalizeForSearch()
    return demoRestaurants.filter { restaurant ->
        val matchesCategory = selectedCategory == null || restaurant.category == selectedCategory
        val matchesSearch = normalizedQuery.isEmpty() ||
            restaurant.name.normalizeForSearch().contains(normalizedQuery) ||
            restaurant.category.normalizeForSearch().contains(normalizedQuery) ||
            restaurant.description.normalizeForSearch().contains(normalizedQuery) ||
            demoProducts.any { product ->
                product.restaurantId == restaurant.id &&
                    (product.name.normalizeForSearch().contains(normalizedQuery) ||
                        product.category.normalizeForSearch().contains(normalizedQuery) ||
                        product.description.normalizeForSearch().contains(normalizedQuery))
            }
        matchesCategory && matchesSearch
    }
}

private fun String.normalizeForSearch(): String = Normalizer
    .normalize(trim(), Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "")
    .lowercase(Locale.ROOT)

@Composable
fun HomeScreen(
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRestaurantClick: (    String) -> Unit,
    onCartClick: () -> Unit,
    cartItemCount: Int,
    modifier: Modifier = Modifier,
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val visibleRestaurants = remember(selectedCategory, searchQuery) {
        filterHomeRestaurants(
            selectedCategory = selectedCategory,
            searchQuery = searchQuery,
        )
    }
    val favoriteRestaurants = visibleRestaurants.sortedByDescending { it.rating }.take(3)
    val nearbyRestaurants = visibleRestaurants.sortedBy { it.distanceMeters }
    val visiblePromotions = demoPromotions.filter { promotion ->
        promotion.validToday && visibleRestaurants.any { it.id == promotion.restaurantId }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = "home",
                onHomeClick = {},
                onMapClick = onMapClick,
                onProfileClick = onProfileClick,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp),
                ) {
                    Spacer(Modifier.height(18.dp))
                    HomeHeader(
                        cartItemCount = cartItemCount,
                        onCartClick = onCartClick,
                    )
                    Spacer(Modifier.height(22.dp))
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClear = { searchQuery = "" },
                    )
                    Spacer(Modifier.height(30.dp))
                    SectionTitle(title = "Explora por categoría")
                    Spacer(Modifier.height(16.dp))
                    CategoryGrid(
                        selectedCategory = selectedCategory,
                        onCategoryClick = { category ->
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                    )
                    if (visibleRestaurants.isEmpty()) {
                        Spacer(Modifier.height(20.dp))
                        EmptySearchResults()
                    }
                    if (visiblePromotions.isNotEmpty()) {
                        Spacer(Modifier.height(34.dp))
                        SectionTitle(
                            title = "Promos de hoy",
                            subtitle = "Ofertas locales por tiempo limitado",
                        )
                    }
                }
            }

            if (visiblePromotions.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(visiblePromotions) { promotion ->
                            PromotionCard(
                                promotion = promotion,
                                onClick = { onRestaurantClick(promotion.restaurantId.toString()) },
                            )
                        }
                    }
                }
            }

            if (visibleRestaurants.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Spacer(Modifier.height(34.dp))
                        SectionTitle(
                            title = if (searchQuery.isBlank()) "Favoritos de estudiantes" else "Resultados",
                            subtitle = if (searchQuery.isBlank()) "Los mejor calificados esta semana" else "Locales y platos que coinciden con tu búsqueda",
                            action = if (searchQuery.isBlank()) "Ver todos" else null,
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(favoriteRestaurants) { restaurant ->
                            RestaurantCard(
                                name = restaurant.name,
                                category = restaurant.category,
                                rating = restaurant.rating.toString(),
                                price = restaurant.priceRange,
                                distance = "${restaurant.distanceMeters} m",
                                imageUrl = restaurant.imageUrl,
                                onClick = { onRestaurantClick(restaurant.id.toString()) },
                            )
                        }
                    }
                }

                if (searchQuery.isBlank()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Spacer(Modifier.height(28.dp))
                            ClassBreakBanner()
                            Spacer(Modifier.height(34.dp))
                            SectionTitle(
                                title = "Cerca de ti",
                                subtitle = "Desde la entrada principal",
                                action = "Ver mapa",
                            )
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    items(nearbyRestaurants) { restaurant ->
                        NearbyRestaurantCard(
                            name = restaurant.name,
                            category = restaurant.category,
                            rating = restaurant.rating.toString(),
                            price = restaurant.priceRange,
                            distance = "${restaurant.distanceMeters} m",
                            deliveryTime = "${(restaurant.distanceMeters / 55).coerceAtLeast(2)} min",
                            imageUrl = restaurant.imageUrl,
                            onClick = { onRestaurantClick(restaurant.id.toString()) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(cartItemCount: Int, onCartClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(15.dp),
            color = PrimaryOrange,
            shadowElevation = 5.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "H",
                    color = WarmSurface,
                    fontWeight = FontWeight.Black,
                    fontSize = 25.sp,
                )
            }
        }
        Spacer(Modifier.width(11.dp))
        Text(
            text = "HAMBRIENTOS",
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 17.sp,
            letterSpacing = 0.6.sp,
        )
        Spacer(Modifier.weight(1f))
        Surface(
            shape = CircleShape,
            color = WarmSurface,
            shadowElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm),
        ) {
            CartBadgeButton(itemCount = cartItemCount, onClick = onCartClick)
        }
    }
    Spacer(Modifier.height(27.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = PrimaryOrange,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "CERCA DE LA UMSS",
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.3.sp,
        )
    }
    Spacer(Modifier.height(5.dp))
    Text(
        text = "¿Qué comemos hoy?",
        color = TextPrimary,
        fontWeight = FontWeight.Black,
        fontSize = 31.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.7).sp,
    )
}

@Composable
private fun PromotionCard(
    promotion: RestaurantPromotion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.width(278.dp),
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(containerColor = AccentYellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = restaurantNameForId(promotion.restaurantId),
                    color = PrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
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
            Spacer(Modifier.height(13.dp))
            Text(
                promotion.title,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                maxLines = 2,
            )
            Text(
                promotion.description,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                promotion.originalPrice?.let { originalPrice ->
                    Text(
                        text = "Bs ${originalPrice.toInt()}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.LineThrough,
                    )
                    Spacer(Modifier.width(7.dp))
                }
                Text(
                    text = "Bs ${promotion.promoPrice.toInt()}",
                    color = PrimaryDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                )
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    contentPadding = PaddingValues(horizontal = 13.dp, vertical = 8.dp),
                ) {
                    Text("Ver promo", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = WarmSurface,
        shadowElevation = 5.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar comida, local o plato...", color = TextSecondary, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, "Buscar", tint = TextSecondary, modifier = Modifier.size(22.dp))
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Close, "Limpiar búsqueda", tint = PrimaryDark)
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtros",
                        tint = PrimaryDark,
                        modifier = Modifier.size(21.dp),
                    )
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryOrange,
            ),
        )
    }
}

@Composable
private fun EmptySearchResults() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = WarmSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm),
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(10.dp))
            Text("No encontramos resultados", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            Spacer(Modifier.height(4.dp))
            Text("Prueba con otro local, producto o categoría.", color = TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun CategoryGrid(
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        categories.chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                rowItems.forEach { category ->
                    CategoryItem(
                        name = category.name,
                        emoji = category.emoji,
                        backgroundColor = category.background,
                        selected = selectedCategory == category.name,
                        onClick = { onCategoryClick(category.name) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    emoji: String,
    backgroundColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(22.dp),
            color = if (selected) PrimaryOrange else backgroundColor,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = 30.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            color = if (selected) PrimaryOrange else TextPrimary,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            maxLines = 2,
        )
    }
}

@Composable
fun RestaurantCard(
    name: String,
    category: String,
    rating: String,
    price: String,
    distance: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(260.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto de $name",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
                    .background(SoftOrange),
                contentScale = ContentScale.Crop,
            )
            RatingBadge(
                rating = rating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            )
            Spacer(Modifier.height(3.dp))
            Text(text = category, color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.height(13.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = price, color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.LocationOn, null, tint = AccentGreen, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(3.dp))
                Text(text = distance, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun RatingBadge(rating: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = WarmSurface.copy(alpha = 0.95f),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Star, null, tint = PrimaryOrange, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(3.dp))
            Text(text = rating, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun NearbyRestaurantCard(
    name: String,
    category: String,
    rating: String,
    price: String,
    distance: String,
    deliveryTime: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto de $name",
                modifier = Modifier
                    .size(width = 96.dp, height = 101.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftOrange),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(13.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 5.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(Icons.Default.Star, null, tint = PrimaryOrange, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(text = rating, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(Modifier.height(3.dp))
                Text(text = category, color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = price, color = PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Text(text = "$deliveryTime  •  $distance", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ClassBreakBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        color = PrimaryDark,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Saliendo de clases?",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Encuentra algo rico a menos de 5 min",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
            Spacer(Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(62.dp),
                shape = CircleShape,
                color = AccentYellow,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🥪", fontSize = 31.sp)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String? = null,
    action: String? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                letterSpacing = (-0.3).sp,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(text = subtitle, color = TextSecondary, fontSize = 12.sp)
            }
        }
        if (action != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SoftOrange,
            ) {
                Text(
                    text = action,
                    color = PrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = WarmSurface,
        tonalElevation = 8.dp,
    ) {
        val items = listOf(
            Triple("Inicio", Icons.Default.Home, "home"),
            Triple("Mapa", Icons.Default.Map, "map"),
            Triple("Perfil", Icons.Default.Person, "profile"),
        )
        items.forEach { (label, icon, route) ->
            val selected = route == selectedRoute
            NavigationBarItem(
                selected = selected,
                onClick = {
                    when (route) {
                        "home" -> onHomeClick()
                        "map" -> onMapClick()
                        "profile" -> onProfileClick()
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryDark,
                    selectedTextColor = PrimaryDark,
                    indicatorColor = SoftOrange,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFAF4, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    HambrientosTheme(darkTheme = false, dynamicColor = false) {
        HomeScreen(
            onMapClick = {},
            onProfileClick = {},
            onRestaurantClick = {},
            onCartClick = {},
            cartItemCount = 2,
        )
    }
}
