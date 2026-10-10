package com.jac.hambrientos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jac.hambrientos.ui.home.HomeScreen
import com.jac.hambrientos.ui.cart.CartScreen
import com.jac.hambrientos.ui.cart.CartState
import com.jac.hambrientos.ui.checkout.CheckoutScreen
import com.jac.hambrientos.ui.map.MapScreen
import com.jac.hambrientos.ui.orders.OrderDetailScreen
import com.jac.hambrientos.ui.orders.OrdersScreen
import com.jac.hambrientos.ui.profile.ProfileScreen
import com.jac.hambrientos.ui.restaurant.RestaurantDetailScreen
import com.jac.hambrientos.ui.reviews.RateScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val cartState = remember { CartState() }
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier,
    ) {
        composable(Routes.Home) {
            HomeScreen(
                onMapClick = { navController.navigateToTopLevel(Routes.Map) },
                onProfileClick = { navController.navigateToTopLevel(Routes.Profile) },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(Routes.restaurant(restaurantId))
                },
                onCartClick = { navController.navigate(Routes.Cart) },
                cartItemCount = cartState.itemCount,
            )
        }

        composable(
            route = Routes.MapDestination,
            arguments = listOf(
                navArgument(Routes.RestaurantId) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            MapScreen(
                destinationRestaurantId = backStackEntry.arguments
                    ?.getString(Routes.RestaurantId),
                onHomeClick = { navController.navigateToTopLevel(Routes.Home) },
                onProfileClick = { navController.navigateToTopLevel(Routes.Profile) },
                onRestaurantClick = { restaurantId ->
                    navController.navigate(Routes.restaurant(restaurantId))
                },
            )
        }

        composable(Routes.Profile) {
            ProfileScreen(
                onHomeClick = { navController.navigateToTopLevel(Routes.Home) },
                onMapClick = { navController.navigateToTopLevel(Routes.Map) },
                onOrdersClick = { navController.navigate(Routes.Orders) },
            )
        }

        composable(
            route = Routes.Restaurant,
            arguments = listOf(
                navArgument(Routes.RestaurantId) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments
                ?.getString(Routes.RestaurantId)
                .orEmpty()
            RestaurantDetailScreen(
                restaurantId = restaurantId,
                onBackClick = { navController.popBackStack() },
                onDirectionsClick = {
                    navController.navigate(Routes.map(restaurantId))
                },
                onRateClick = { navController.navigate(Routes.rate(restaurantId)) },
                onCartClick = { navController.navigate(Routes.Cart) },
                cartState = cartState,
            )
        }

        composable(Routes.Cart) {
            CartScreen(
                cartState = cartState,
                onBackClick = { navController.popBackStack() },
                onContinueClick = { navController.navigate(Routes.Checkout) },
            )
        }

        composable(Routes.Checkout) {
            CheckoutScreen(
                cartState = cartState,
                onBackClick = { navController.popBackStack() },
                onReturnHome = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Home) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onViewOrders = {
                    navController.navigate(Routes.Orders) {
                        popUpTo(Routes.Checkout) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.Orders) {
            OrdersScreen(
                onBackClick = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate(Routes.order(orderId)) },
            )
        }

        composable(
            route = Routes.Order,
            arguments = listOf(
                navArgument(Routes.OrderId) { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            OrderDetailScreen(
                orderId = backStackEntry.arguments?.getInt(Routes.OrderId) ?: -1,
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.Rate,
            arguments = listOf(
                navArgument(Routes.RestaurantId) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments
                ?.getString(Routes.RestaurantId)
                .orEmpty()
            RateScreen(
                restaurantId = restaurantId,
                onBackClick = { navController.popBackStack() },
                onReturnToRestaurant = {
                    val returned = navController.popBackStack(
                        route = Routes.restaurant(restaurantId),
                        inclusive = false,
                    )
                    if (!returned) {
                        navController.navigate(Routes.restaurant(restaurantId)) {
                            popUpTo(Routes.Rate) { inclusive = true }
                        }
                    }
                },
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
