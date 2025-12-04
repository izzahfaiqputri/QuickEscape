package com.example.quickescape.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.quickescape.data.FoodData
import com.example.quickescape.data.model.Food
import com.example.quickescape.data.model.PaymentRequest
import com.example.quickescape.data.model.PaymentResponse
import com.example.quickescape.network.PaymentApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FoodRepository(private val context: Context) {

    private val paymentApiService = PaymentApiService.create()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getFoodsByLocationId(locationId: String): List<Food> {
        Log.d("FoodRepository", "=== FOOD MATCHING DEBUG ===")
        Log.d("FoodRepository", "Input locationId: '$locationId'")
        Log.d("FoodRepository", "LocationId length: ${locationId.length}")
        Log.d("FoodRepository", "LocationId isEmpty: ${locationId.isEmpty()}")

        if (locationId.isEmpty()) {
            Log.e("FoodRepository", "LocationId is EMPTY! Returning empty list.")
            return emptyList()
        }

        return try {
            // Get location name from Firestore using the locationId
            val locationDoc = firestore.collection("locations").document(locationId).get().await()
            val locationName = locationDoc.getString("name")

            Log.d("FoodRepository", "Location name from Firestore: '$locationName'")

            if (locationName.isNullOrEmpty()) {
                Log.e("FoodRepository", "No location found for ID: $locationId")
                return emptyList()
            }

            // Match foods by location name
            val matchingFoods = FoodData.foods.filter { food ->
                food.locationName.equals(locationName, ignoreCase = true)
            }

            Log.d("FoodRepository", "Found ${matchingFoods.size} foods for location: $locationName")
            matchingFoods.forEach { food ->
                Log.d("FoodRepository", "✅ ${food.name} - Rp ${food.price}")
            }

            matchingFoods

        } catch (e: Exception) {
            Log.e("FoodRepository", "Error fetching location or foods: ${e.message}")

            // Fallback: try direct ID matching for backward compatibility
            val directMatches = FoodData.foods.filter { food ->
                food.locationId.equals(locationId, ignoreCase = true)
            }

            if (directMatches.isNotEmpty()) {
                Log.d("FoodRepository", "Found ${directMatches.size} foods using direct ID matching")
                return directMatches
            }

            // If no matches found, return empty list
            Log.d("FoodRepository", "No foods found for location ID: $locationId")
            emptyList()
        }
    }

    suspend fun createPayment(food: Food, quantity: Int): PaymentResponse? {
        return try {
            Log.d("FoodRepository", "=== CREATING PAYMENT ===")

            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid ?: "guest"

            Log.d("FoodRepository", "User ID: $userId")

            val totalAmount = food.price * quantity
            val description = "${quantity}x ${food.name}"

            Log.d("FoodRepository", "Amount: $totalAmount")
            Log.d("FoodRepository", "Description: $description")

            // Generate unique order ID for tracking
            val orderId = "QE${System.currentTimeMillis()}${(1000..9999).random()}"

            // Deep link URLs untuk redirect setelah payment
            val successUrl = "quickescape://payment?orderId=$orderId&status=success"
            val failureUrl = "quickescape://payment?orderId=$orderId&status=failed"

            Log.d("FoodRepository", "Success redirect URL: $successUrl")
            Log.d("FoodRepository", "Failure redirect URL: $failureUrl")

            val request = PaymentRequest(
                amount = totalAmount,
                description = description,
                userId = userId,
                successRedirectUrl = successUrl,
                failureRedirectUrl = failureUrl
            )

            Log.d("FoodRepository", "Sending request to API...")
            Log.d("FoodRepository", "Request: amount=$totalAmount, desc=$description, userId=$userId")

            val response = paymentApiService.createPayment(request)

            Log.d("FoodRepository", "Response code: ${response.code()}")
            Log.d("FoodRepository", "Response message: ${response.message()}")

            if (response.isSuccessful) {
                val paymentResponse = response.body()
                Log.d("FoodRepository", "✅ Payment created successfully!")
                Log.d("FoodRepository", "Invoice URL: ${paymentResponse?.invoiceUrl}")
                Log.d("FoodRepository", "External ID: ${paymentResponse?.externalId}")
                paymentResponse
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("FoodRepository", "❌ Payment creation failed!")
                Log.e("FoodRepository", "Error code: ${response.code()}")
                Log.e("FoodRepository", "Error message: ${response.message()}")
                Log.e("FoodRepository", "Error body: $errorBody")
                null
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("FoodRepository", "❌ Network error: Cannot reach payment server")
            Log.e("FoodRepository", "Check your internet connection")
            null
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("FoodRepository", "❌ Timeout: Payment server not responding")
            null
        } catch (e: Exception) {
            Log.e("FoodRepository", "❌ Error creating payment: ${e.message}")
            Log.e("FoodRepository", "Error type: ${e.javaClass.simpleName}")
            e.printStackTrace()
            null
        }
    }

    fun openPaymentUrl(paymentUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error opening payment URL", e)
        }
    }
}
