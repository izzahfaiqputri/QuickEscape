package com.example.quickescape

import android.Manifest
import android.content.Intent
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
                            modifier = Modifier.weight(1f),
                            startDestination = determineStartDestination()
                        )

                        // Bottom Navigation
                        val navBackStackEntry = navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry.value?.destination?.route

                        if (currentRoute in listOf(
                                Screen.Home.route,
                                Screen.Explore.route,
                                Screen.Search.route,
                                Screen.AskAI.route,
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

                    // Handle deep link dari Intent
                    androidx.compose.runtime.LaunchedEffect(intent.data) {
                        handleDeepLink(navController, intent)
                    }
                }
            }
        }
    }

    private fun handleDeepLink(navController: androidx.navigation.NavController, intentData: Intent) {
        val data = intentData.data

        Log.d(TAG, "Checking deep link: scheme=${data?.scheme}, host=${data?.host}")

        if (data != null && data.scheme == "quickescape" && data.host == "payment") {
            val orderId = data.getQueryParameter("orderId")
            val status = data.getQueryParameter("status") // "success" or "failed"

            Log.d(TAG, "✅ Payment callback received!")
            Log.d(TAG, "Order ID: $orderId")
            Log.d(TAG, "Status: $status")

            if (!orderId.isNullOrEmpty() && status == "success") {
                // Payment berhasil - Navigate to invoice screen
                Log.d(TAG, "Navigating to invoice screen for order: $orderId")

                // Delay sedikit untuk memastikan NavController sudah siap
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    navController.navigate(Screen.Invoice.createRoute(orderId)) {
                        // Clear back stack to prevent going back to payment
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }

                    // Show success message
                    android.widget.Toast.makeText(
                        this,
                        "Payment successful! Loading invoice...",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }, 500)
            } else {
                // Payment failed
                Log.e(TAG, "Payment failed or cancelled")
                android.widget.Toast.makeText(
                    this,
                    "Payment ${status ?: "cancelled"}. Please try again.",
                    android.widget.Toast.LENGTH_LONG
                ).show()

                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
                }
            }
        } else {
            Log.d(TAG, "No payment deep link found in intent")
        }
    }

    private fun determineStartDestination(): String {
        // Always start from onboarding regardless of authentication status
        return Screen.Onboarding.route
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val data = intent.data
        if (data != null && data.scheme == "quickescape" && data.host == "payment") {
            Log.d(TAG, "New intent received with payment callback")

            val orderId = data.getQueryParameter("orderId")
            val status = data.getQueryParameter("status")

            Log.d(TAG, "Payment callback - Order ID: $orderId, Status: $status")

        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
