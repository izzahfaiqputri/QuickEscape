package com.example.quickescape.data.model

data class OrderInfo(
    val customerName: String = "",
    val phoneNumber: String = "",
    val food: Food? = null,
    val quantity: Int = 1,
    val totalAmount: Int = 0,
    val orderId: String = "",
    val paymentUrl: String = "",
    val status: String = "pending" // pending, paid, failed
)

data class Invoice(
    val orderId: String = "",
    val customerName: String = "",
    val phoneNumber: String = "",
    val foodName: String = "",
    val quantity: Int = 1,
    val pricePerItem: Int = 0,
    val totalAmount: Int = 0,
    val paymentMethod: String = "",
    val paymentDate: String = "",
    val status: String = "paid"
)
