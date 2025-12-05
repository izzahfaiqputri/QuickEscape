package com.example.quickescape.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quickescape.data.helper.FirebaseInitializer
import com.example.quickescape.data.repository.LocationRepository
import com.example.quickescape.data.viewmodel.LocationViewModel
import com.example.quickescape.ui.auth.AuthScreen
import com.example.quickescape.ui.auth.SignInScreen
import com.example.quickescape.ui.auth.SignUpScreen
import com.example.quickescape.ui.onboarding.OnboardingScreen
import com.example.quickescape.ui.home.AddReviewScreen
import com.example.quickescape.ui.home.DetailLocationScreen
import com.example.quickescape.ui.home.ExploreScreen
import com.example.quickescape.ui.home.HomeScreen
import com.example.quickescape.ui.home.SearchScreen
import com.example.quickescape.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import android.util.Log

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                onSignInClick = {
                    navController.navigate(Screen.SignIn.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onSignInSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignInClick = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val auth = FirebaseAuth.getInstance()
            val repository = LocationRepository(firestore, storage)
            val userRepository = com.example.quickescape.data.repository.UserRepository(firestore, storage, auth)

            val viewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(repository) as T
                    }
                }
            )

            val userViewModel: com.example.quickescape.data.viewmodel.UserViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return com.example.quickescape.data.viewmodel.UserViewModel(userRepository) as T
                    }
                }
            )

            val locations by viewModel.locations.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val userProfile by userViewModel.userProfile.collectAsState()
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    FirebaseInitializer.initializeLocations(firestore)
                    viewModel.loadLocations()
                    userViewModel.loadUserProfile()
                }
            }

            HomeScreen(
                locations = locations,
                isLoading = isLoading,
                onLocationClick = { location ->
                    navController.navigate(Screen.DetailLocation.createRoute(location.id))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onCategoryClick = { category ->
                    if (category != "All") {
                        viewModel.filterByCategory(category)
                    } else {
                        viewModel.loadLocations()
                    }
                },
                onMenuClick = {
                    navController.navigate(Screen.Profile.route)
                },
                userProfileImage = userProfile?.profileImage ?: ""
            )
        }

        // Search Screen
        composable(Screen.Search.route) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val repository = LocationRepository(firestore, storage)

            val viewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(repository) as T
                    }
                }
            )

            val locations by viewModel.locations.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadLocations()
            }

            SearchScreen(
                locations = locations,
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onSearch = { query ->
                    if (query.isNotEmpty()) {
                        viewModel.searchLocations(query)
                    } else {
                        viewModel.loadLocations()
                    }
                },
                onLocationClick = { location ->
                    navController.navigate(Screen.DetailLocation.createRoute(location.id))
                }
            )
        }

        composable(
            Screen.DetailLocation.route,
            arguments = listOf(
                navArgument("locationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val auth = FirebaseAuth.getInstance()
            val context = androidx.compose.ui.platform.LocalContext.current
            val locationRepository = LocationRepository(firestore, storage)
            val userRepository = com.example.quickescape.data.repository.UserRepository(firestore, storage, auth)
            val foodRepository = com.example.quickescape.repository.FoodRepository(context)

            val locationViewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(locationRepository) as T
                    }
                }
            )

            val userViewModel: com.example.quickescape.data.viewmodel.UserViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return com.example.quickescape.data.viewmodel.UserViewModel(userRepository) as T
                    }
                }
            )

            val selectedLocation by locationViewModel.selectedLocation.collectAsState()
            val reviews by locationViewModel.reviews.collectAsState()
            val isLoading by locationViewModel.isLoading.collectAsState()
            val userProfile by userViewModel.userProfile.collectAsState()
            val locationPhotos by locationViewModel.locationPhotos.collectAsState()
            val isUploadingPhoto by locationViewModel.isUploadingPhoto.collectAsState()

            // State to hold foods for this location
            var foods by remember { mutableStateOf<List<com.example.quickescape.data.model.Food>>(emptyList()) }
            var isFoodsLoading by remember { mutableStateOf(true) }

            val coroutineScope = rememberCoroutineScope()

            // Load foods when locationId changes
            LaunchedEffect(locationId) {
                Log.d("NavGraph", "=== LOCATION DEBUG ===")
                Log.d("NavGraph", "Current locationId from navigation: '$locationId'")
                Log.d("NavGraph", "LocationId length: ${locationId.length}")
                Log.d("NavGraph", "LocationId isEmpty: ${locationId.isEmpty()}")

                if (selectedLocation != null) {
                    Log.d("NavGraph", "Selected location name: '${selectedLocation!!.name}'")
                    Log.d("NavGraph", "Selected location.id: '${selectedLocation!!.id}'")
                } else {
                    Log.d("NavGraph", "Selected location is NULL")
                }

                isFoodsLoading = true
                try {
                    val result = foodRepository.getFoodsByLocationId(locationId)
                    foods = result
                    Log.d("NavGraph", "FoodRepository returned ${result.size} foods")
                    Log.d("NavGraph", "====================")
                } catch (e: Exception) {
                    Log.e("NavGraph", "Error loading foods: ${e.message}")
                    foods = emptyList()
                } finally {
                    isFoodsLoading = false
                }
            }

            // Reload photos setiap kali screen ini di-compose (termasuk saat kembali dari Camera)
            LaunchedEffect(locationId) {
                locationViewModel.loadLocationById(locationId)
                locationViewModel.loadLocationPhotos(locationId)
                userViewModel.loadUserProfile()
            }

            // reload photos saat navBackStackEntry berubah (kembali dari Camera)
            val navBackStackEntry = navController.currentBackStackEntry
            LaunchedEffect(navBackStackEntry) {
                locationViewModel.loadLocationPhotos(locationId)
            }

            // Derive isSaved directly from userProfile to always be in sync
            val isSaved = userProfile?.savedLocations?.contains(locationId) ?: false

            if (selectedLocation != null) {
                DetailLocationScreen(
                    location = selectedLocation!!,
                    reviews = reviews,
                    isLoading = isLoading,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddReviewClick = {
                        navController.navigate(Screen.AddReview.createRoute(locationId))
                    },
                    onSaveClick = { locId ->
                        if (isSaved) {
                            userViewModel.removeSavedLocation(locId)
                        } else {
                            userViewModel.addSavedLocation(locId)
                        }
                    },
                    onDeleteReview = { reviewId ->
                        locationViewModel.deleteReview(locationId, reviewId)
                    },
                    isSaved = isSaved,
                    photos = locationPhotos,
                    onAddPhotoClick = {
                        navController.navigate(Screen.Camera.createRoute(locationId))
                    },
                    onDeletePhoto = { photoUrl ->
                        locationViewModel.deleteLocationPhoto(locationId, photoUrl)
                    },
                    isUploadingPhoto = isUploadingPhoto,
                    foods = foods,
                    onOrderFood = { food, quantity ->
                        // Navigate to order form instead of direct payment
                        navController.navigate(Screen.OrderForm.createRoute(locationId, food.id))
                    }
                )
            }
        }

        composable(
            Screen.Camera.route,
            arguments = listOf(
                navArgument("locationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val repository = LocationRepository(firestore, storage)
            val viewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(repository) as T
                    }
                }
            )

            val coroutineScope = rememberCoroutineScope()
            val context = androidx.compose.ui.platform.LocalContext.current

            com.example.quickescape.ui.camera.CameraScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPhotoTaken = { photoUri ->
                    // Show toast immediately
                    android.widget.Toast.makeText(
                        context,
                        "Uploading photo...",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

                    // Upload foto dulu, baru pop back setelah selesai
                    coroutineScope.launch {
                        try {
                            android.util.Log.d("CameraScreen", "Starting photo upload process...")
                            viewModel.addLocationPhoto(locationId, photoUri)

                            // Tampilkan success message
                            android.widget.Toast.makeText(
                                context,
                                "Photo uploaded successfully!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()

                            android.util.Log.d("CameraScreen", "Upload complete, navigating back...")
                            kotlinx.coroutines.delay(500) // small delay untuk pastikan UI update
                            navController.popBackStack()
                        } catch (e: Exception) {
                            android.util.Log.e("CameraScreen", "Upload failed: ${e.message}", e)
                            android.widget.Toast.makeText(
                                context,
                                "Upload failed: ${e.message}",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            e.printStackTrace()
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

        composable(
            Screen.AddReview.route,
            arguments = listOf(
                navArgument("locationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val repository = LocationRepository(firestore, storage)
            val viewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(repository) as T
                    }
                }
            )

            val selectedLocation by viewModel.selectedLocation.collectAsState()

            LaunchedEffect(locationId) {
                viewModel.loadLocationById(locationId)
            }

            if (selectedLocation != null) {
                AddReviewScreen(
                    locationName = selectedLocation!!.name,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSubmitClick = { review, photoUris ->
                        viewModel.addReview(locationId, review, photoUris)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.Explore.route) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val repository = LocationRepository(firestore, storage)
            val viewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(repository) as T
                    }
                }
            )

            var userLocation by remember { mutableStateOf("") }
            var userLat by remember { mutableStateOf(0.0) }
            var userLon by remember { mutableStateOf(0.0) }
            val nearbyLocations by viewModel.nearbyLocations.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val context = androidx.compose.ui.platform.LocalContext.current

            // Request location permissions dan get user location
            LaunchedEffect(Unit) {
                val locationManager = com.example.quickescape.data.helper.LocationManager(context)
                val lastLocation = locationManager.getLastKnownLocation()
                if (lastLocation != null) {
                    userLat = lastLocation.latitude
                    userLon = lastLocation.longitude
                    userLocation = "${String.format("%.4f", userLat)}, ${String.format("%.4f", userLon)}"
                    viewModel.loadNearbyLocations(userLat, userLon, 50f)
                } else {
                    userLocation = "Location not available"
                }
                locationManager.startLocationUpdates()
            }

            ExploreScreen(
                nearbyLocations = nearbyLocations,
                isLoading = isLoading,
                userLocation = userLocation,
                onLocationClick = { location ->
                    navController.navigate(Screen.DetailLocation.createRoute(location.id))
                },
                onRetryClick = {
                    val locationManager = com.example.quickescape.data.helper.LocationManager(context)
                    val lastLocation = locationManager.getLastKnownLocation()
                    if (lastLocation != null) {
                        userLat = lastLocation.latitude
                        userLon = lastLocation.longitude
                        userLocation = "${String.format("%.4f", userLat)}, ${String.format("%.4f", userLon)}"
                        viewModel.loadNearbyLocations(userLat, userLon, 50f)
                    }
                }
            )
        }

        composable(
            Screen.OrderForm.route,
            arguments = listOf(
                navArgument("locationId") { type = NavType.StringType },
                navArgument("foodId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: ""
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            val context = androidx.compose.ui.platform.LocalContext.current
            val foodRepository = com.example.quickescape.repository.FoodRepository(context)

            val orderViewModel: com.example.quickescape.data.viewmodel.OrderViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return com.example.quickescape.data.viewmodel.OrderViewModel(foodRepository) as T
                    }
                }
            )

            // Find the food from FoodData
            val food = com.example.quickescape.data.FoodData.foods.find { it.id == foodId }
            val currentOrder by orderViewModel.currentOrder.collectAsState()
            val currentInvoice by orderViewModel.currentInvoice.collectAsState()
            val isLoading by orderViewModel.isLoading.collectAsState()
            val errorMessage by orderViewModel.errorMessage.collectAsState()

            // Handle error messages and navigation
            errorMessage?.let { error ->
                LaunchedEffect(error) {
                    when {
                        error == "payment_api_offline" -> {
                            // Payment API is offline, navigate directly to invoice
                            android.widget.Toast.makeText(
                                context,
                                "Payment server unavailable. Showing order confirmation.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()

                            // Navigate to invoice with current order ID
                            currentOrder?.let { order ->
                                navController.navigate(Screen.Invoice.createRoute(order.orderId)) {
                                    popUpTo(Screen.DetailLocation.route) {
                                        inclusive = false
                                    }
                                }
                            }
                            orderViewModel.clearError()
                        }
                        error.startsWith("error_") -> {
                            // Other errors
                            val errorMsg = error.removePrefix("error_")
                            android.widget.Toast.makeText(
                                context,
                                "Error: $errorMsg",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            orderViewModel.clearError()
                        }
                        else -> {
                            android.widget.Toast.makeText(
                                context,
                                error,
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            orderViewModel.clearError()
                        }
                    }
                }
            }

            // Navigate to payment when order is created successfully
            LaunchedEffect(currentOrder) {
                currentOrder?.let { order ->
                    if (order.status == "pending" && order.paymentUrl.isNotEmpty()) {
                        android.widget.Toast.makeText(
                            context,
                            "Redirecting to payment...",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            if (food != null) {
                com.example.quickescape.ui.order.OrderFormScreen(
                    food = food,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onOrderSubmit = { customerName, phoneNumber, quantity ->
                        orderViewModel.createOrder(food, customerName, phoneNumber, quantity)
                    }
                )
            } else {
                // Handle food not found
                LaunchedEffect(Unit) {
                    android.widget.Toast.makeText(context, "Food not found", android.widget.Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
        }

        composable(
            Screen.Invoice.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val context = androidx.compose.ui.platform.LocalContext.current
            val foodRepository = com.example.quickescape.repository.FoodRepository(context)

            val orderViewModel: com.example.quickescape.data.viewmodel.OrderViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return com.example.quickescape.data.viewmodel.OrderViewModel(foodRepository) as T
                    }
                }
            )

            val currentInvoice by orderViewModel.currentInvoice.collectAsState()

            // Simulate payment success when this screen loads
            LaunchedEffect(orderId) {
                orderViewModel.handlePaymentSuccess(orderId, "OVO")
            }

            currentInvoice?.let { invoice ->
                com.example.quickescape.ui.order.InvoiceScreen(
                    invoice = invoice,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onDoneClick = {
                        // Navigate back to detail location and clear the order
                        orderViewModel.clearOrder()
                        navController.popBackStack(Screen.DetailLocation.route, false)
                    }
                )
            }
        }

        composable(Screen.Profile.route) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val locationRepository = LocationRepository(firestore, storage)
            val userRepository = com.example.quickescape.data.repository.UserRepository(firestore, storage, auth)

            val locationViewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(locationRepository) as T
                    }
                }
            )

            val userViewModel: com.example.quickescape.data.viewmodel.UserViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return com.example.quickescape.data.viewmodel.UserViewModel(userRepository) as T
                    }
                }
            )

            val userProfile by userViewModel.userProfile.collectAsState()
            val savedLocations by userViewModel.savedLocations.collectAsState()
            val isLoading by userViewModel.isLoading.collectAsState()
            val allLocations by locationViewModel.locations.collectAsState()

            LaunchedEffect(Unit) {
                userViewModel.loadUserProfile()
                locationViewModel.loadLocations()
            }

            LaunchedEffect(allLocations) {
                if (allLocations.isNotEmpty()) {
                    userViewModel.loadSavedLocations(allLocations)
                }
            }

            ProfileScreen(
                userProfile = userProfile,
                savedLocations = savedLocations,
                isLoading = isLoading,
                onUpdateProfileImage = { imageUri ->
                    userViewModel.updateProfileImage(imageUri)
                },
                onLocationClick = { location ->
                    navController.navigate(Screen.DetailLocation.createRoute(location.id))
                },
                onRemoveSavedLocation = { locationId ->
                    userViewModel.removeSavedLocation(locationId)
                },
                onSaveProfileChanges = { newName ->
                    userViewModel.updateProfileInfo(newName, currentUser?.email ?: "")
                }
            )
        }

        composable(Screen.AskAI.route) {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val repository = LocationRepository(firestore, storage)
            val context = androidx.compose.ui.platform.LocalContext.current

            val locationViewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(repository) as T
                    }
                }
            )

            val travelAIViewModel: com.example.quickescape.data.viewmodel.TravelAIViewModel = viewModel()

            val locations by locationViewModel.locations.collectAsState()

            var userLat by remember { mutableStateOf(0.0) }
            var userLon by remember { mutableStateOf(0.0) }

            LaunchedEffect(Unit) {
                locationViewModel.loadLocations()

                val locationManager = com.example.quickescape.data.helper.LocationManager(context)
                val lastLocation = locationManager.getLastKnownLocation()
                if (lastLocation != null) {
                    userLat = lastLocation.latitude
                    userLon = lastLocation.longitude
                    travelAIViewModel.setUserLocation(userLat, userLon)
                }
            }

            LaunchedEffect(locations) {
                if (locations.isNotEmpty()) {
                    travelAIViewModel.setLocations(locations)
                }
            }

            com.example.quickescape.ui.home.AskAIScreen(
                viewModel = travelAIViewModel,
                onLocationClick = { location ->
                    navController.navigate(Screen.DetailLocation.createRoute(location.id))
                }
            )
        }
    }
}
