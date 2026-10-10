package com.jac.hambrientos.ui.cart

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jac.hambrientos.data.local.CartItem
import com.jac.hambrientos.data.local.restaurantNameForId
import com.jac.hambrientos.ui.theme.OutlineWarm
import com.jac.hambrientos.ui.theme.PrimaryDark
import com.jac.hambrientos.ui.theme.PrimaryOrange
import com.jac.hambrientos.ui.theme.SoftOrange
import com.jac.hambrientos.ui.theme.TextPrimary
import com.jac.hambrientos.ui.theme.TextSecondary
import com.jac.hambrientos.ui.theme.WarmBackground
import com.jac.hambrientos.ui.theme.WarmSurface

@Composable
fun CartScreen(
    cartState: CartState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = { CartHeader(onBackClick) },
        bottomBar = {
            if (cartState.items.isNotEmpty()) {
                CartSummaryBar(
                    subtotal = cartState.subtotal,
                    onContinueClick = onContinueClick,
                )
            }
        },
    ) { innerPadding ->
        if (cartState.items.isEmpty()) {
            EmptyCart(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        text = restaurantNameForId(cartState.restaurantId),
                        color = PrimaryDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
                items(cartState.items, key = { it.product.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrement = { cartState.increment(item.product.id) },
                        onDecrement = { cartState.decrement(item.product.id) },
                        onRemove = { cartState.remove(item.product.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CartHeader(onBackClick: () -> Unit) {
    Surface(color = WarmSurface, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary)
            }
            Text(
                text = "Tu carrito",
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineWarm),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(SoftOrange),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = item.product.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            "Eliminar",
                            tint = TextSecondary,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                }
                Text(
                    text = formatBs(item.product.price * item.quantity),
                    color = PrimaryDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuantityButton(Icons.Default.Remove, "Disminuir", onDecrement)
                    Text(
                        text = item.quantity.toString(),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp),
                    )
                    QuantityButton(Icons.Default.Add, "Aumentar", onIncrement)
                }
            }
        }
    }
}

@Composable
private fun QuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Surface(shape = CircleShape, color = SoftOrange) {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(icon, description, tint = PrimaryDark, modifier = Modifier.size(17.dp))
        }
    }
}

@Composable
private fun CartSummaryBar(subtotal: Double, onContinueClick: () -> Unit) {
    Surface(color = WarmSurface, shadowElevation = 12.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Subtotal", color = TextSecondary, modifier = Modifier.weight(1f))
                Text(
                    formatBs(subtotal),
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onContinueClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            ) {
                Text("Continuar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmptyCart(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(shape = CircleShape, color = SoftOrange) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier.padding(22.dp).size(40.dp),
            )
        }
        Spacer(Modifier.height(18.dp))
        Text("Tu carrito está vacío", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(6.dp))
        Text("Agrega algo rico desde el menú de un local.", color = TextSecondary, fontSize = 13.sp)
    }
}

private fun formatBs(value: Double): String =
    if (value % 1.0 == 0.0) "Bs ${value.toInt()}" else "Bs %.2f".format(value)
