package com.example.quickescape.data.model

data class PaymentRequest(
    val amount: Int,
    val description: String,
    val userId: String,
    val successRedirectUrl: String,
    val failureRedirectUrl: String
)

data class PaymentResponse(
    val amount: Int,
    val externalId: String,
    val invoiceUrl: String,
    val status: String
)
