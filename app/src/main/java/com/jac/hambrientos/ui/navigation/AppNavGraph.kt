package com.jac.hambrientos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jac.hambrientos.ui.home.HomeScreen
import com.jac.hambrientos.ui.map.MapScreen
import com.jac.hambrientos.ui.profile.ProfileScreen
import com.jac.hambrientos.ui.restaurant.RestaurantDetailScreen
import com.jac.hambrientos.ui.reviews.RateScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
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
            )
        }

        composable(Routes.Map) {
            MapScreen(
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
                onRateClick = { navController.navigate(Routes.rate(restaurantId)) },
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
