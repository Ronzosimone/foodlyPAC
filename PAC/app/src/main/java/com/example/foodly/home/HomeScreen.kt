package com.example.foodly.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
// Import new placeholder screens
import com.example.foodly.pantry.PantryScreen
import com.example.foodly.recipes.RecipeDetailScreen
import com.example.foodly.recipes.RecipesScreen
import com.example.foodly.settings.SettingsScreen
import com.example.foodly.statistics.StatisticsScreen

// Define navigation routes specifically for screens reachable from HomeScreen's BottomNavigation
object HomeNavRoutes {
    const val RICETTE = "home_ricette"
    const val STATISTICHE = "home_statistiche"
    const val DISPENSA = "home_dispensa"
    const val IMPOSTAZIONI = "home_impostazioni"
    const val RECIPE_DETAIL = "home_recipe_detail/{recipeId}"

    fun createRecipeDetailRoute(recipeId: Int) = "home_recipe_detail/$recipeId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit) { // Added onLogout callback
    val navController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Apply background to Scaffold
        bottomBar = { BottomNavigation(navController) }
    ) { paddingValues ->
        // Ensure the content area uses the theme's background color
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeNavRoutes.RICETTE // Use new route object
                // modifier = Modifier.padding(paddingValues) // Padding is now on the Surface
            ) {
                composable(HomeNavRoutes.RICETTE) {
                    // Pass the NavController to RecipesScreen
                    com.example.foodly.recipes.RecipesScreen()
                }
                composable(HomeNavRoutes.STATISTICHE) {
                    // New placeholder StatisticsScreen
                    com.example.foodly.statistics.StatisticsScreen()
                }
                composable(HomeNavRoutes.IMPOSTAZIONI) {
                    // New placeholder SettingsScreen, pass the onLogout callback
                    com.example.foodly.settings.SettingsScreen(onLogout = onLogout)
                }
                composable(HomeNavRoutes.DISPENSA) {
                    // New placeholder PantryScreen
                    com.example.foodly.pantry.PantryScreen()
                }
                composable(
                    route = HomeNavRoutes.RECIPE_DETAIL,
                    arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments?.getInt("recipeId")
                    requireNotNull(recipeId) { "Recipe ID not found in arguments" }
                    RecipeDetailScreen(
                        recipeId = recipeId,
                        onNavigateBack = { navController.navigateUp() }
                        // ViewModel will be accessed via viewModel() in RecipeDetailScreen itself
                    )
                }
            }
        }
    }
}

// Internal Navigation items for BottomNavigation
private sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Ricette : BottomNavItem(HomeNavRoutes.RICETTE, Icons.Outlined.Menu, "Ricette")
    object Statistiche : BottomNavItem(HomeNavRoutes.STATISTICHE, Icons.Outlined.Info, "Statistica")
    object Dispensa : BottomNavItem(HomeNavRoutes.DISPENSA, Icons.Filled.ShoppingCart, "Dispensa")
    object Impostazioni : BottomNavItem(HomeNavRoutes.IMPOSTAZIONI, Icons.Filled.Settings, "Impostazioni")
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Ricette,
        BottomNavItem.Statistiche,
        BottomNavItem.Dispensa,
        BottomNavItem.Impostazioni
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp) // Apply surfaceColorAtElevation
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    com.example.foodly.ui.theme.FoodlyTheme { // Apply FoodlyTheme to Preview
        HomeScreen(onLogout = {}) // Provide dummy callback for preview
    }
}