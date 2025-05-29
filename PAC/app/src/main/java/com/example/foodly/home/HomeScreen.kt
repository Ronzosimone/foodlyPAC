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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.foodly.recipes.RecipesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigation(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Ricette.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavigationItem.Ricette.route) {
                RicetteScreen()
            }
            composable(NavigationItem.Statistiche.route) {
                StatisticheScreen()
            }
            composable(NavigationItem.Impostazioni.route) {
                ImpostazioniScreen()
            }
            composable(NavigationItem.Dispensa.route) {
                DispensaScreen()
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        NavigationItem.Ricette,
        NavigationItem.Statistiche,
        NavigationItem.Dispensa,
        NavigationItem.Impostazioni
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

// Schermate (Screens)
@Composable
fun RicetteScreen() {
}

@Composable
fun StatisticheScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Statistiche",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DispensaScreen() {
    RecipesScreen()
}

@Composable
fun ImpostazioniScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Impostazioni",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Definizione degli elementi di navigazione
sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Ricette : NavigationItem("ricette", Icons.Outlined.Menu, "Ricette")
    object Statistiche : NavigationItem("statistiche", Icons.Outlined.Info, "Statistiche")
    object Dispensa : NavigationItem("dispensa", Icons.Filled.ShoppingCart, "Dispensa")
    object Impostazioni : NavigationItem("impostazioni", Icons.Filled.Settings, "Profilo")
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}