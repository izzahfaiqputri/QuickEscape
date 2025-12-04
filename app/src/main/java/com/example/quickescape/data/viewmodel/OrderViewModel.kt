package com.example.quickescape.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickescape.data.model.Food
import com.example.quickescape.data.model.Invoice
import com.example.quickescape.data.model.OrderInfo
import com.example.quickescape.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _currentOrder = MutableStateFlow<OrderInfo?>(null)
    val currentOrder: StateFlow<OrderInfo?> = _currentOrder.asStateFlow()

    private val _currentInvoice = MutableStateFlow<Invoice?>(null)
    val currentInvoice: StateFlow<Invoice?> = _currentInvoice.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun createOrder(
        food: Food,
        customerName: String,
        phoneNumber: String,
        quantity: Int
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val totalAmount = food.price * quantity
                val orderId = generateOrderId()

                Log.d("OrderViewModel", "=== CREATING ORDER ===")
                Log.d("OrderViewModel", "Order ID: $orderId")
                Log.d("OrderViewModel", "Food: ${food.name}")
                Log.d("OrderViewModel", "Customer: $customerName")
                Log.d("OrderViewModel", "Phone: $phoneNumber")
                Log.d("OrderViewModel", "Quantity: $quantity")
                Log.d("OrderViewModel", "Total: Rp $totalAmount")

                // Store order info first
                val orderInfo = OrderInfo(
                    customerName = customerName,
                    phoneNumber = phoneNumber,
                    food = food,
                    quantity = quantity,
                    totalAmount = totalAmount,
                    orderId = orderId,
                    paymentUrl = "",
                    status = "pending"
                )
                _currentOrder.value = orderInfo

                // Try to create payment via API
                Log.d("OrderViewModel", "Calling payment API...")
                val paymentResponse = foodRepository.createPayment(food, quantity)

                if (paymentResponse != null) {
                    Log.d("OrderViewModel", "✅ Payment response received")
                    Log.d("OrderViewModel", "Invoice URL: ${paymentResponse.invoiceUrl}")

                    // Update order with payment URL
                    _currentOrder.value = orderInfo.copy(
                        paymentUrl = paymentResponse.invoiceUrl
                    )

                    // Open payment URL
                    foodRepository.openPaymentUrl(paymentResponse.invoiceUrl)
                } else {
                    Log.e("OrderViewModel", "❌ Payment API failed, using fallback method")

                    // FALLBACK: Create mock invoice for testing when API is down
                    val currentDate = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault()).format(Date())

                    val mockInvoice = Invoice(
                        orderId = orderId,
                        customerName = customerName,
                        phoneNumber = phoneNumber,
                        foodName = food.name,
                        quantity = quantity,
                        pricePerItem = food.price,
                        totalAmount = totalAmount,
                        paymentMethod = "Cash/Direct Payment",
                        paymentDate = currentDate,
                        status = "pending_confirmation"
                    )

                    _currentInvoice.value = mockInvoice
                    _errorMessage.value = "payment_api_offline" // Special flag for navigation

                    Log.d("OrderViewModel", "✅ Mock invoice created for testing")
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "❌ Error creating order: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "error_${e.message ?: "Unknown error occurred"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handlePaymentSuccess(
        orderId: String,
        paymentMethod: String = "OVO" // Default from screenshot
    ) {
        val order = _currentOrder.value
        if (order != null && order.orderId == orderId) {
            val currentDate = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault()).format(Date())

            val invoice = Invoice(
                orderId = order.orderId,
                customerName = order.customerName,
                phoneNumber = order.phoneNumber,
                foodName = order.food?.name ?: "",
                quantity = order.quantity,
                pricePerItem = order.food?.price ?: 0,
                totalAmount = order.totalAmount,
                paymentMethod = paymentMethod,
                paymentDate = currentDate,
                status = "paid"
            )

            _currentInvoice.value = invoice
            Log.d("OrderViewModel", "Payment successful for order: $orderId")
        }
    }

    fun clearOrder() {
        _currentOrder.value = null
        _currentInvoice.value = null
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun generateOrderId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "QE$timestamp$random"
    }
}
