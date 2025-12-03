package com.example.quickescape

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.quickescape.navigation.NavGraph
import com.example.quickescape.navigation.Screen
import com.example.quickescape.notification.LocationTrackingService
import com.example.quickescape.notification.NotificationHelper
import com.example.quickescape.ui.navigation.BottomNavigationBar
import com.example.quickescape.ui.theme.QuickEscapeTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private lateinit var locationTrackingService: LocationTrackingService
    private lateinit var notificationHelper: NotificationHelper

    // Request notification permission launcher
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
            // Get FCM token
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM Token: $token")
                }
            }
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize services
        locationTrackingService = LocationTrackingService(this)
        notificationHelper = NotificationHelper(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Check if user is logged in and start location tracking
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Log.d(TAG, "User logged in, starting location tracking")
            locationTrackingService.startLocationTracking()
        }

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

    override fun onResume() {
        super.onResume()
        // Check if we need to navigate to a specific location (from notification)
        intent?.extras?.let { extras ->
            val shouldNavigate = extras.getBoolean("navigate_to_detail", false)
            val locationId = extras.getString("locationId")

            if (shouldNavigate && locationId != null) {
                Log.d(TAG, "Navigating to location: $locationId")
                // Navigation will be handled by the NavGraph
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
