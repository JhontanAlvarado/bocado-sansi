package com.jac.hambrientos.ui.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jac.hambrientos.data.local.DemoOrder
import com.jac.hambrientos.data.local.DemoOrderRepository
import com.jac.hambrientos.data.local.OrderStatus
import com.jac.hambrientos.ui.theme.AccentGreen
import com.jac.hambrientos.ui.theme.AccentYellow
import com.jac.hambrientos.ui.theme.OutlineWarm
import com.jac.hambrientos.ui.theme.PrimaryOrange
import com.jac.hambrientos.ui.theme.SoftGreen
import com.jac.hambrientos.ui.theme.SoftOrange
import com.jac.hambrientos.ui.theme.TextPrimary
import com.jac.hambrientos.ui.theme.TextSecondary
import com.jac.hambrientos.ui.theme.WarmBackground
import com.jac.hambrientos.ui.theme.WarmSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBackClick: () -> Unit,
    onOrderClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val orders = DemoOrderRepository.getOrders()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos", fontWeight = FontWeight.Black, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmSurface),
            )
        },
    ) { innerPadding ->
        if (orders.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, tint = PrimaryOrange)
                Spacer(Modifier.height(14.dp))
                Text("Aún no tienes pedidos", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(Modifier.height(6.dp))
                Text("Cuando confirmes uno, aparecerá aquí.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(orders, key = DemoOrder::id) { order ->
                    OrderCard(order = order, onClick = { onOrderClick(order.id) })
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: DemoOrder, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, OutlineWarm.copy(alpha = 0.7f)),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pedido #${order.id}", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 17.sp)
                    Text(order.restaurantName, color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    Text(order.createdAt, color = TextSecondary, fontSize = 12.sp)
                }
                OrderStatusBadge(order.status)
            }
            Spacer(Modifier.height(14.dp))
            order.items.forEach { item ->
                Text("${item.quantity}x ${item.productName}", color = TextPrimary, fontSize = 13.sp)
            }
            Spacer(Modifier.height(14.dp))
            Row {
                Text("Total", color = TextSecondary, modifier = Modifier.weight(1f))
                Text(formatBs(order.total), color = TextPrimary, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
internal fun OrderStatusBadge(status: OrderStatus) {
    val (label, colors) = when (status) {
        OrderStatus.PENDING -> "Pendiente" to (AccentYellow to TextPrimary)
        OrderStatus.PREPARING -> "Preparando" to (SoftOrange to PrimaryOrange)
        OrderStatus.READY -> "Listo" to (SoftGreen to AccentGreen)
        OrderStatus.COMPLETED -> "Completado" to (Color(0xFFE8E6E3) to TextSecondary)
    }
    Surface(shape = RoundedCornerShape(50), color = colors.first) {
        Text(label, color = colors.second, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

internal fun formatBs(value: Double): String =
    if (value % 1.0 == 0.0) "Bs ${value.toInt()}" else "Bs %.2f".format(value)
