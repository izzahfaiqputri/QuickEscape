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
import com.example.quickescape.ui.home.HomeScreen
import com.example.quickescape.ui.home.SearchScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    FirebaseInitializer.initializeLocations(firestore)
                    viewModel.loadLocations()
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
                    // TODO: Handle menu click
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
            val repository = LocationRepository(firestore, storage)
            val viewModel: LocationViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return LocationViewModel(repository) as T
                    }
                }
            )

            val selectedLocation by viewModel.selectedLocation.collectAsState()
            val reviews by viewModel.reviews.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            LaunchedEffect(locationId) {
                viewModel.loadLocationById(locationId)
            }

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
                    onSaveClick = {
                        // TODO: Save to user's saved locations
                    },
                    onDeleteReview = { reviewId ->
                        viewModel.deleteReview(locationId, reviewId)
                    }
                )
            }
        }

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

            SearchScreen(
                locations = locations,
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onSearch = { query ->
                    viewModel.searchLocations(query)
                },
                onLocationClick = { location ->
                    navController.navigate(Screen.DetailLocation.createRoute(location.id))
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
    }
}
