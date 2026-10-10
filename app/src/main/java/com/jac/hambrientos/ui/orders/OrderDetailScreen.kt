package com.jac.hambrientos.ui.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jac.hambrientos.data.local.DemoOrderRepository
import com.jac.hambrientos.ui.theme.OutlineWarm
import com.jac.hambrientos.ui.theme.PrimaryOrange
import com.jac.hambrientos.ui.theme.TextPrimary
import com.jac.hambrientos.ui.theme.TextSecondary
import com.jac.hambrientos.ui.theme.WarmBackground
import com.jac.hambrientos.ui.theme.WarmSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val order = DemoOrderRepository.getOrderById(orderId)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del pedido", color = TextPrimary, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmSurface),
            )
        },
    ) { innerPadding ->
        if (order == null) {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp)) {
                Text("No encontramos este pedido.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.dp, OutlineWarm),
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Pedido #${order.id}", color = TextSecondary, fontSize = 12.sp)
                            Text(order.restaurantName, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 22.sp)
                            Text(order.createdAt, color = TextSecondary, fontSize = 13.sp)
                            Spacer(Modifier.height(14.dp))
                            OrderStatusBadge(order.status)
                        }
                    }
                }
                item {
                    Text("Productos", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                        border = BorderStroke(1.dp, OutlineWarm),
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            order.items.forEach { item ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${item.quantity}x ${item.productName}", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                        Text("${formatBs(item.unitPrice)} c/u", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    Text(formatBs(item.unitPrice * item.quantity), color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("Total", color = TextSecondary, modifier = Modifier.weight(1f))
                                Text(formatBs(order.total), color = PrimaryOrange, fontWeight = FontWeight.Black, fontSize = 19.sp)
                            }
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmSurface),
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Text("Método de pago", color = TextSecondary, modifier = Modifier.weight(1f))
                            Text(order.paymentMethod, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
