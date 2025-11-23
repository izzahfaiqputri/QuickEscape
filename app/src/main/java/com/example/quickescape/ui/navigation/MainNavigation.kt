package com.example.quickescape.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.quickescape.navigation.Screen

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.background(Color.White),
        containerColor = Color.White,
        contentColor = Color(0xFFE8725E)
    ) {
        // Home
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            selected = currentRoute == Screen.Home.route,
            onClick = { onNavigate(Screen.Home.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE8725E),
                selectedTextColor = Color(0xFFE8725E),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.White
            )
        )

        // Explore
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Explore,
                    contentDescription = "Explore"
                )
            },
            label = { Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            selected = currentRoute == Screen.Explore.route,
            onClick = { onNavigate(Screen.Explore.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE8725E),
                selectedTextColor = Color(0xFFE8725E),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.White
            )
        )

        // Messages
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Mail,
                    contentDescription = "Messages"
                )
            },
            label = { Text("Messages", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            selected = false,
            onClick = { /* TODO: Navigate to Messages */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE8725E),
                selectedTextColor = Color(0xFFE8725E),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.White
            )
        )

        // Profile
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            selected = false,
            onClick = { /* TODO: Navigate to Profile */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE8725E),
                selectedTextColor = Color(0xFFE8725E),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.White
            )
        )
    }
}
