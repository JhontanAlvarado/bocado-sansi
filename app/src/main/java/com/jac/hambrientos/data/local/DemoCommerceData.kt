package com.jac.hambrientos.data.local

private const val BurgerImage = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=80"
private const val HomeFoodImage = "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=80"
private const val PizzaImage = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=900&q=80"
private const val CoffeeImage = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=900&q=80"
private const val ChickenImage = "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?auto=format&fit=crop&w=900&q=80"
private const val HealthyImage = "https://images.unsplash.com/photo-1543362906-acfc16c67564?auto=format&fit=crop&w=900&q=80"
private const val FriesImage = "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=700&q=80"
private const val SodaImage = "https://images.unsplash.com/photo-1581636625402-29b2a704ef13?auto=format&fit=crop&w=700&q=80"
private const val SandwichImage = "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=700&q=80"

val demoRestaurants = listOf(
    Restaurant(1, "La Esquina Sansi", "Hamburguesas", "Bs 7–25", 4.2, 120, BurgerImage, "11:00–22:00", "Hamburguesas abundantes y combos pensados para estudiantes.", -17.3928, -66.1446),
    Restaurant(2, "Burger Lab", "Hamburguesas", "Bs 12–32", 4.3, 193, BurgerImage, "11:30–22:30", "Hamburguesas artesanales, salsas de la casa y papas crocantes.", -17.3941, -66.1438),
    Restaurant(3, "Doña Meche", "Comida casera", "Bs 5–25", 4.5, 160, HomeFoodImage, "10:30–20:30", "Almuerzos caseros con el sabor de siempre y porciones generosas.", -17.3947, -66.1461),
    Restaurant(4, "Pizza Patio", "Pizza", "Bs 18–55", 4.4, 230, PizzaImage, "12:00–22:30", "Pizzas recién horneadas para comer solo o compartir.", -17.3919, -66.1468),
    Restaurant(5, "Café Central", "Cafeterías", "Bs 10–24", 4.6, 80, CoffeeImage, "07:30–20:00", "Café, sándwiches y un espacio tranquilo cerca del campus.", -17.3931, -66.1459),
    Restaurant(6, "Pollos 15", "Pollo", "Bs 18–58", 4.5, 140, ChickenImage, "11:00–22:00", "Pollo dorado y combos rápidos a precio estudiantil.", -17.3950, -66.1445),
    Restaurant(7, "Verde Menta", "Saludable", "Bs 10–24", 4.7, 210, HealthyImage, "09:00–20:00", "Bowls, ensaladas y jugos frescos preparados al momento.", -17.3922, -66.1437),
)

val demoProducts = listOf(
    RestaurantProduct(1, 1, "Hamburguesas", "Hamburguesa Sansi", "Carne, queso y salsa de la casa", 18.0, BurgerImage),
    RestaurantProduct(2, 1, "Hamburguesas", "Doble Sansi", "Doble carne, doble queso y salsa Sansi", 25.0, BurgerImage),
    RestaurantProduct(3, 1, "Combos", "Combo estudiantil", "Hamburguesa, papas y gaseosa", 22.0, BurgerImage),
    RestaurantProduct(4, 1, "Acompañamientos", "Papas fritas", "Papas doradas y crocantes", 10.0, FriesImage),
    RestaurantProduct(5, 1, "Bebidas", "Gaseosa", "Bebida personal fría", 7.0, SodaImage),
    RestaurantProduct(6, 2, "Hamburguesas", "Burger clásica", "Carne, queso, lechuga y tomate", 20.0, BurgerImage),
    RestaurantProduct(7, 2, "Hamburguesas", "Doble burger", "Doble carne y doble queso", 28.0, BurgerImage),
    RestaurantProduct(8, 2, "Hamburguesas", "Burger BBQ", "Carne, queso, tocino y salsa BBQ", 26.0, BurgerImage),
    RestaurantProduct(9, 2, "Combos", "Combo Lab", "Burger clásica, papas y bebida", 32.0, BurgerImage),
    RestaurantProduct(10, 2, "Acompañamientos", "Papas Lab", "Papas sazonadas al estilo Lab", 12.0, FriesImage),
    RestaurantProduct(11, 3, "Platos", "Silpancho", "Carne apanada, arroz, papa, huevo y ensalada", 22.0, HomeFoodImage),
    RestaurantProduct(12, 3, "Platos", "Milanesa", "Milanesa de carne con arroz y papas", 20.0, HomeFoodImage),
    RestaurantProduct(13, 3, "Platos", "Pique pequeño", "Carne, salchicha, papas y verduras", 25.0, HomeFoodImage),
    RestaurantProduct(14, 3, "Sopas", "Sopa del día", "Sopa casera preparada cada mañana", 10.0, HomeFoodImage),
    RestaurantProduct(15, 3, "Bebidas", "Refresco", "Refresco natural del día", 5.0, SodaImage),
    RestaurantProduct(16, 4, "Pizza", "Pizza personal", "Pizza de 4 porciones con sabor a elección", 18.0, PizzaImage),
    RestaurantProduct(17, 4, "Pizza", "Pizza mediana", "Pizza de 8 porciones con sabor a elección", 35.0, PizzaImage),
    RestaurantProduct(18, 4, "Pizza", "Pizza familiar", "Pizza familiar de 12 porciones", 55.0, PizzaImage),
    RestaurantProduct(19, 4, "Combos", "Combo pizza + gaseosa", "Pizza personal con gaseosa", 24.0, PizzaImage),
    RestaurantProduct(20, 5, "Café", "Café americano", "Café filtrado recién preparado", 10.0, CoffeeImage),
    RestaurantProduct(21, 5, "Café", "Capuccino", "Espresso con leche cremosa", 16.0, CoffeeImage),
    RestaurantProduct(22, 5, "Café", "Mocaccino", "Espresso, chocolate y leche cremosa", 18.0, CoffeeImage),
    RestaurantProduct(23, 5, "Sándwiches", "Sandwich mixto", "Jamón y queso tostados", 18.0, SandwichImage),
    RestaurantProduct(24, 5, "Combos", "Café + sandwich", "Café americano con sandwich mixto", 24.0, SandwichImage),
    RestaurantProduct(25, 6, "Pollo", "1/4 pollo", "Cuarto de pollo con papas y ensalada", 18.0, ChickenImage),
    RestaurantProduct(26, 6, "Pollo", "1/2 pollo", "Medio pollo con papas y ensalada", 32.0, ChickenImage),
    RestaurantProduct(27, 6, "Pollo", "Pollo completo", "Pollo completo con papas y ensalada", 58.0, ChickenImage),
    RestaurantProduct(28, 6, "Combos", "Combo estudiantil", "Cuarto de pollo, papas y refresco", 22.0, ChickenImage),
    RestaurantProduct(29, 7, "Ensaladas", "Ensalada César", "Lechuga, pollo, crutones y aderezo César", 20.0, HealthyImage),
    RestaurantProduct(30, 7, "Bowls", "Bowl de pollo", "Pollo, arroz, verduras y palta", 24.0, HealthyImage),
    RestaurantProduct(31, 7, "Sándwiches", "Sandwich integral", "Pan integral, vegetales y proteína", 18.0, SandwichImage),
    RestaurantProduct(32, 7, "Bebidas", "Jugo natural", "Fruta de temporada preparada al momento", 10.0, HealthyImage),
)

val demoPromotions = listOf(
    RestaurantPromotion(1, 1, "Combo estudiantil", "Hamburguesa, papas y gaseosa", 26.0, 22.0, true),
    RestaurantPromotion(2, 2, "2 hamburguesas + papas", "Ideal para compartir después de clases", null, 35.0, true),
    RestaurantPromotion(3, 5, "Café + sandwich", "Una pausa completa para recargar energías", null, 24.0, true),
)

fun restaurantById(restaurantId: Int): Restaurant? = demoRestaurants.firstOrNull { it.id == restaurantId }
fun productsForRestaurant(restaurantId: Int): List<RestaurantProduct> = demoProducts.filter { it.restaurantId == restaurantId }
fun promotionsForRestaurant(restaurantId: Int): List<RestaurantPromotion> = demoPromotions.filter { it.restaurantId == restaurantId && it.validToday }
fun restaurantNameForId(restaurantId: Int?): String = restaurantId?.let(::restaurantById)?.name ?: "Hambrientos"
