package com.jac.hambrientos.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jac.hambrientos.ui.home.BottomNavigationBar
import com.jac.hambrientos.ui.theme.AccentGreen
import com.jac.hambrientos.ui.theme.OutlineWarm
import com.jac.hambrientos.ui.theme.PrimaryDark
import com.jac.hambrientos.ui.theme.PrimaryOrange
import com.jac.hambrientos.ui.theme.SoftGreen
import com.jac.hambrientos.ui.theme.SoftOrange
import com.jac.hambrientos.ui.theme.TextPrimary
import com.jac.hambrientos.ui.theme.TextSecondary
import com.jac.hambrientos.ui.theme.WarmBackground
import com.jac.hambrientos.ui.theme.WarmSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi perfil",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 21.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmSurface),
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = "profile",
                onHomeClick = onHomeClick,
                onMapClick = onMapClick,
                onProfileClick = {},
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, top = 26.dp, end = 20.dp, bottom = 30.dp),
        ) {
            item {
                ProfileHeader()
                Spacer(Modifier.height(25.dp))
                ProfileStatsCard()
                Spacer(Modifier.height(30.dp))
                ProfileSectionTitle("Mi actividad")
                Spacer(Modifier.height(12.dp))
                ProfileMenuCard(
                    items = listOf(
                        ProfileMenuEntry("Mis reseñas", Icons.Default.RateReview),
                        ProfileMenuEntry("Mis favoritos", Icons.Default.Favorite),
                        ProfileMenuEntry("Promociones guardadas", Icons.Default.Bookmark),
                    ),
                )
                Spacer(Modifier.height(30.dp))
                ProfileSectionTitle("Cuenta")
                Spacer(Modifier.height(12.dp))
                ProfileMenuCard(
                    items = listOf(
                        ProfileMenuEntry("Editar perfil", Icons.Default.Edit),
                        ProfileMenuEntry("Configuración", Icons.Default.Settings),
                        ProfileMenuEntry("Ayuda", Icons.AutoMirrored.Filled.HelpOutline),
                        ProfileMenuEntry("Cerrar sesión", Icons.AutoMirrored.Filled.ExitToApp),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(124.dp)) {
            Surface(
                modifier = Modifier
                    .size(112.dp)
                    .align(Alignment.Center),
                shape = CircleShape,
                color = PrimaryOrange,
                shadowElevation = 6.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "AM",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 38.sp,
                        letterSpacing = 1.sp,
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .align(Alignment.BottomEnd),
                shape = CircleShape,
                color = AccentGreen,
                border = BorderStroke(3.dp, WarmBackground),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Perfil verificado",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Ana Martínez",
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 25.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(text = "Estudiante UMSS", color = TextSecondary, fontSize = 14.sp)
        Spacer(Modifier.height(12.dp))
        VerifiedBadge()
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = SoftGreen,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(15.dp),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "Estudiante verificado",
                color = AccentGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun ProfileStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 21.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileStat("12", "Reseñas", Icons.Default.RateReview, Modifier.weight(1f))
            StatDivider()
            ProfileStat("8", "Favoritos", Icons.Default.Favorite, Modifier.weight(1f))
            StatDivider()
            ProfileStat("120", "Puntos", Icons.Default.Star, Modifier.weight(1f))
        }
    }
}

@Composable
fun ProfileStat(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, tint = PrimaryOrange, modifier = Modifier.size(19.dp))
        Spacer(Modifier.height(6.dp))
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 19.sp)
        Text(text = label, color = TextSecondary, fontWeight = FontWeight.Medium, fontSize = 11.sp)
    }
}

@Composable
private fun StatDivider() {
    Surface(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp),
        color = OutlineWarm,
    ) {}
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        color = TextPrimary,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 19.sp,
        letterSpacing = (-0.2).sp,
    )
}

private data class ProfileMenuEntry(
    val title: String,
    val icon: ImageVector,
)

@Composable
private fun ProfileMenuCard(items: List<ProfileMenuEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.65f)),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                ProfileMenuItem(
                    text = item.title,
                    icon = item.icon,
                    onClick = {},
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 72.dp),
                        color = OutlineWarm,
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(13.dp),
            color = SoftOrange,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryDark,
                    modifier = Modifier.size(21.dp),
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(22.dp),
        )
    }
}
