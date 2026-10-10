package com.jac.hambrientos.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jac.hambrientos.data.local.restaurantNameForId
import com.jac.hambrientos.data.local.DemoOrder
import com.jac.hambrientos.data.local.DemoOrderRepository
import com.jac.hambrientos.data.local.OrderItem
import com.jac.hambrientos.data.local.OrderStatus
import com.jac.hambrientos.ui.cart.CartState
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private enum class PaymentMethod(val label: String, val description: String) {
    Cash("Efectivo", "Paga al recoger tu pedido"),
    Qr("QR", "Simulación para la demo; no se realizará ningún cobro"),
}

private data class OrderReceipt(
    val restaurantName: String,
    val total: Double,
    val paymentMethod: String,
)

@Composable
fun CheckoutScreen(
    cartState: CartState,
    onBackClick: () -> Unit,
    onReturnHome: () -> Unit,
    onViewOrders: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var paymentMethod by remember { mutableStateOf(PaymentMethod.Cash) }
    var receipt by remember { mutableStateOf<OrderReceipt?>(null) }

    receipt?.let {
        OrderSuccessScreen(
            receipt = it,
            onViewOrders = onViewOrders,
            onReturnHome = onReturnHome,
            modifier = modifier,
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = WarmBackground,
        topBar = { CheckoutHeader(onBackClick) },
        bottomBar = {
            if (cartState.items.isNotEmpty()) {
                Surface(color = WarmSurface, shadowElevation = 12.dp) {
                    Button(
                        onClick = {
                            val restaurantId = cartState.restaurantId ?: return@Button
                            val restaurantName = restaurantNameForId(restaurantId)
                            val total = cartState.subtotal
                            val order = DemoOrder(
                                id = DemoOrderRepository.nextOrderId(),
                                restaurantId = restaurantId,
                                restaurantName = restaurantName,
                                items = cartState.items.map { item ->
                                    OrderItem(
                                        productName = item.product.name,
                                        quantity = item.quantity,
                                        unitPrice = item.product.price,
                                    )
                                },
                                total = total,
                                paymentMethod = paymentMethod.label,
                                status = OrderStatus.PREPARING,
                                createdAt = LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"),
                                ),
                            )
                            DemoOrderRepository.addOrder(order)
                            receipt = OrderReceipt(
                                restaurantName = restaurantName,
                                total = total,
                                paymentMethod = paymentMethod.label,
                            )
                            cartState.clear()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    ) {
                        Text("Confirmar pedido", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { CheckoutSectionTitle("Resumen") }
            items(cartState.items, key = { it.product.id }) { item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${item.quantity} × ${item.product.name}",
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                    )
                    Text(
                        text = formatBs(item.product.price * item.quantity),
                        color = PrimaryDark,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SoftOrange,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("Subtotal", color = TextSecondary, modifier = Modifier.weight(1f))
                        Text(formatBs(cartState.subtotal), color = TextPrimary, fontWeight = FontWeight.Black)
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                CheckoutSectionTitle("Método de pago")
            }
            items(PaymentMethod.entries) { method ->
                PaymentOption(
                    method = method,
                    selected = paymentMethod == method,
                    onSelect = { paymentMethod = method },
                )
            }
        }
    }
}

@Composable
private fun CheckoutHeader(onBackClick: () -> Unit) {
    Surface(color = WarmSurface, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary)
            }
            Text("Finalizar pedido", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 22.sp)
        }
    }
}

@Composable
private fun PaymentOption(method: PaymentMethod, selected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onSelect),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSurface),
        border = BorderStroke(1.5.dp, if (selected) PrimaryOrange else OutlineWarm),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = selected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange),
            )
            Column(modifier = Modifier.padding(start = 6.dp)) {
                Text(method.label, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(method.description, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun OrderSuccessScreen(
    receipt: OrderReceipt,
    onViewOrders: () -> Unit,
    onReturnHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().background(WarmBackground).statusBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(shape = CircleShape, color = SoftGreen) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.padding(20.dp),
            )
        }
        Spacer(Modifier.height(22.dp))
        Text("¡Pedido realizado!", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 27.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tu pedido fue registrado para la demo.",
            color = TextSecondary,
            fontSize = 14.sp,
        )
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSurface),
            border = BorderStroke(1.dp, OutlineWarm),
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReceiptRow("Restaurante", receipt.restaurantName)
                ReceiptRow("Total", formatBs(receipt.total))
                ReceiptRow("Método de pago", receipt.paymentMethod)
            }
        }
        Spacer(Modifier.height(26.dp))
        Button(
            onClick = onViewOrders,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
        ) {
            Text("Ver mis pedidos", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onReturnHome,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
        ) {
            Text("Volver al inicio", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CheckoutSectionTitle(title: String) {
    Text(title, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
}

private fun formatBs(value: Double): String =
    if (value % 1.0 == 0.0) "Bs ${value.toInt()}" else "Bs %.2f".format(value)
