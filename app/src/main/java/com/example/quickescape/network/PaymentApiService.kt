package com.example.quickescape.network

import com.example.quickescape.data.model.PaymentRequest
import com.example.quickescape.data.model.PaymentResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {
    @POST("create-payment")
    suspend fun createPayment(@Body request: PaymentRequest): Response<PaymentResponse>

    companion object {
        private const val BASE_URL = "https://quickescape-payment-api.onrender.com/"

        fun create(): PaymentApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PaymentApiService::class.java)
        }
    }
}
