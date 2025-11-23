package com.example.quickescape

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.quickescape.navigation.NavGraph
import com.example.quickescape.navigation.Screen
import com.example.quickescape.ui.navigation.BottomNavigationBar
import com.example.quickescape.ui.theme.QuickEscapeTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickEscapeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    Column(modifier = Modifier.fillMaxSize()) {
                        // NavGraph content
                        NavGraph(
                            navController = navController,
                            modifier = Modifier.weight(1f)
                        )

                        // Bottom Navigation
                        val navBackStackEntry = navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry.value?.destination?.route

                        // Tampilkan bottom nav hanya di home, explore, search, dan profile
                        if (currentRoute in listOf(
                                Screen.Home.route,
                                Screen.Explore.route,
                                Screen.Search.route,
                                Screen.Profile.route
                            )) {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
