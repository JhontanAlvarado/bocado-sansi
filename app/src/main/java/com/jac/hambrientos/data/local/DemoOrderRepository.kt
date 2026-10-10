package com.jac.hambrientos.data.local

import androidx.compose.runtime.mutableStateListOf

data class DemoOrder(
    val id: Int,
    val restaurantId: Int,
    val restaurantName: String,
    val items: List<OrderItem>,
    val total: Double,
    val paymentMethod: String,
    val status: OrderStatus,
    val createdAt: String,
)

data class OrderItem(
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
)

enum class OrderStatus {
    PENDING,
    PREPARING,
    READY,
    COMPLETED,
}

object DemoOrderRepository {
    private val orders = mutableStateListOf<DemoOrder>()
    private var nextId = 1001

    fun nextOrderId(): Int = nextId++

    fun addOrder(order: DemoOrder) {
        orders.add(0, order)
    }

    fun getOrders(): List<DemoOrder> = orders

    fun getOrderById(id: Int): DemoOrder? = orders.firstOrNull { it.id == id }
}
