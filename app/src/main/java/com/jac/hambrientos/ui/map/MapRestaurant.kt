package com.jac.hambrientos.ui.map

import org.maplibre.android.geometry.LatLng

data class MapRestaurant(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val rating: Double,
    val distance: String,
    val position: LatLng,
    val imageUrl: String,
    val isUnderBs15: Boolean,
)

val demoMapRestaurants = listOf(
    MapRestaurant("1", "La Esquina Sansi", "Hamburguesas", "Bs 10–18", 4.2, "120 m", LatLng(-17.3928, -66.1446), "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=600&q=80", true),
    MapRestaurant("2", "Burger Lab", "Comida casera", "Bs 15–25", 4.3, "193 m", LatLng(-17.3941, -66.1438), "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=600&q=80", false),
    MapRestaurant("3", "Doña Meche", "Comida casera", "Bs 12–20", 4.5, "160 m", LatLng(-17.3947, -66.1461), "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=600&q=80", true),
    MapRestaurant("4", "Pizza Patio", "Pizza", "Bs 18–32", 4.4, "230 m", LatLng(-17.3919, -66.1468), "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=600&q=80", false),
    MapRestaurant("5", "Café Central", "Cafetería", "Bs 8–22", 4.6, "80 m", LatLng(-17.3931, -66.1459), "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=600&q=80", true),
    MapRestaurant("6", "Pollos 15", "Pollo y combos", "Bs 15–30", 4.5, "140 m", LatLng(-17.3950, -66.1445), "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?auto=format&fit=crop&w=600&q=80", false),
    MapRestaurant("7", "Verde Menta", "Comida saludable", "Bs 18–32", 4.7, "210 m", LatLng(-17.3922, -66.1437), "https://images.unsplash.com/photo-1543362906-acfc16c67564?auto=format&fit=crop&w=600&q=80", false),
)
