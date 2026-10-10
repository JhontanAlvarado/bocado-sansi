package com.jac.hambrientos.ui.cart

import androidx.compose.runtime.mutableStateListOf
import com.jac.hambrientos.data.local.CartItem
import com.jac.hambrientos.data.local.RestaurantProduct

sealed interface AddToCartResult {
    data object Added : AddToCartResult
    data object DifferentRestaurant : AddToCartResult
}

class CartState {
    private val mutableItems = mutableStateListOf<CartItem>()
    val items: List<CartItem> get() = mutableItems

    val restaurantId: Int? get() = mutableItems.firstOrNull()?.product?.restaurantId
    val itemCount: Int get() = mutableItems.sumOf(CartItem::quantity)
    val subtotal: Double get() = mutableItems.sumOf { it.product.price * it.quantity }

    fun addProduct(product: RestaurantProduct): AddToCartResult {
        if (restaurantId != null && restaurantId != product.restaurantId) {
            return AddToCartResult.DifferentRestaurant
        }
        val index = mutableItems.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val item = mutableItems[index]
            mutableItems[index] = item.copy(quantity = item.quantity + 1)
        } else {
            mutableItems += CartItem(product, quantity = 1)
        }
        return AddToCartResult.Added
    }

    fun increment(productId: Int) {
        val item = mutableItems.firstOrNull { it.product.id == productId } ?: return
        addProduct(item.product)
    }

    fun decrement(productId: Int) {
        val index = mutableItems.indexOfFirst { it.product.id == productId }
        if (index < 0) return
        val item = mutableItems[index]
        if (item.quantity <= 1) mutableItems.removeAt(index)
        else mutableItems[index] = item.copy(quantity = item.quantity - 1)
    }

    fun remove(productId: Int) {
        mutableItems.removeAll { it.product.id == productId }
    }

    fun clear() = mutableItems.clear()
}
