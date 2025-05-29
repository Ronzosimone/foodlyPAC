package com.example.foodly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodly.home.HomeScreen
import com.example.foodly.login.LoginScreen
import com.example.foodly.login.RegistrationScreen
import com.example.foodly.ui.theme.FoodlyTheme

object AppRoutes {
    const val LOGIN = "login"
    const val REGISTRATION = "registration"
    const val HOME = "home"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodlyTheme {
                FoodlyApp()
            }
        }
    }
}

@Composable
fun FoodlyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN, // Start with Login screen
        modifier = modifier.fillMaxSize()
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true } // Clear login from back stack
                    }
                },
                onNavigateToRegister = { navController.navigate(AppRoutes.REGISTRATION) }
            )
        }
        composable(AppRoutes.REGISTRATION) {
            RegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.REGISTRATION) { inclusive = true } // Clear registration from back stack
                    }
                }
            )
        }
        composable(AppRoutes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.HOME) { inclusive = true } // Clear Home from back stack
                        launchSingleTop = true // Avoid multiple copies of Login
                    }
                }
            )
        }
    }
}

// Preview can be simplified or removed if it causes issues with NavController
// Preview for FoodlyApp might need adjustment if NavController is causing issues in preview mode
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FoodlyTheme {
        // FoodlyApp() // This might be problematic for previews with complex NavHost.
                       // Consider previewing individual screens or a simplified NavHost.
        Text("App Preview (See individual screen previews for details)")
    }
}