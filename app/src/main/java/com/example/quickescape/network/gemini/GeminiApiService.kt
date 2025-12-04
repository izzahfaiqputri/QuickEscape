package com.example.quickescape.network.gemini

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Base URL: https://generativelanguage.googleapis.com/
 * - gemini-2.5-flash
 * - gemini-2.5-pro
 * - gemini-2.0-flash
 */
interface GeminiApiService {
    @POST("v1/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): GeminiResponse
}
