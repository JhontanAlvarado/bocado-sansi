package com.jac.hambrientos.ui.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeRestaurantFilterTest {
    @Test
    fun emptySearchShowsEveryRestaurant() {
        assertEquals((1..7).toList(), filterHomeRestaurants(null, "").map { it.id })
    }

    @Test
    fun searchesNamesCategoriesDescriptionsAndProductsIgnoringAccentsAndCase() {
        assertTrue(filterHomeRestaurants(null, "Burger").any { it.id == 2 })
        assertEquals(listOf(5), filterHomeRestaurants(null, "Café").map { it.id })
        assertEquals(listOf(5), filterHomeRestaurants(null, "cafe").map { it.id })
        assertTrue(filterHomeRestaurants(null, "Pizza").any { it.id == 4 })
        assertTrue(filterHomeRestaurants(null, "pollo").any { it.id == 6 })
        assertEquals(listOf(5), filterHomeRestaurants(null, "capuccino").map { it.id })
        assertTrue(filterHomeRestaurants(null, "xyz").isEmpty())
    }

    @Test
    fun categoryAndSearchMustBothMatch() {
        assertEquals(
            listOf(2),
            filterHomeRestaurants("Hamburguesas", "lab").map { it.id },
        )
        assertTrue(filterHomeRestaurants("Pizza", "lab").isEmpty())
    }
}
