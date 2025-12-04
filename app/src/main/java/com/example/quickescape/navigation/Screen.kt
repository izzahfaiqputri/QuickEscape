package com.example.quickescape.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object Explore : Screen("explore")
    object AskAI : Screen("ask_ai")
    object Profile : Screen("profile")
    object DetailLocation : Screen("detail_location/{locationId}") {
        fun createRoute(locationId: String) = "detail_location/$locationId"
    }
    object Search : Screen("search")
    object AddReview : Screen("add_review/{locationId}") {
        fun createRoute(locationId: String) = "add_review/$locationId"
    }
    object Camera : Screen("camera/{locationId}") {
        fun createRoute(locationId: String) = "camera/$locationId"
    }
    object OrderForm : Screen("order_form/{locationId}/{foodId}") {
        fun createRoute(locationId: String, foodId: String) = "order_form/$locationId/$foodId"
    }
    object Invoice : Screen("invoice/{orderId}") {
        fun createRoute(orderId: String) = "invoice/$orderId"
    }
}
