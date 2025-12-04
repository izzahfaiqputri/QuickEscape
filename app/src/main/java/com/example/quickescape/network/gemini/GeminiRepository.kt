package com.example.quickescape.network.gemini

import com.example.quickescape.BuildConfig
import android.util.Log
import retrofit2.HttpException

class GeminiRepository {
    private val apiService = GeminiClient.apiService
    private val apiKey = BuildConfig.GEMINI_API_KEY

    companion object {
        private const val TAG = "GeminiRepository"
        const val SYSTEM_PROMPT = "You are QuickEscape AI — an assistant that ONLY answers travel-related questions such as tourism recommendations, places nearby, locations, or destinations. If the user asks unrelated questions (math, personal help, cooking, etc), politely decline and redirect them back to travel topics. Keep responses concise and helpful."
    }

    /**
     * Mengirim prompt ke Gemini dan mendapatkan response
     *
     * @param userPrompt User input pertanyaan
     * @return Response text dari Gemini
     */
    suspend fun askGemini(userPrompt: String): String {
        return try {
            // Validasi input
            if (userPrompt.isBlank()) {
                return "Please enter a question about travel destinations."
            }

            // Log API Key status
            Log.d(TAG, "API Key configured: ${apiKey.isNotEmpty()}")
            Log.d(TAG, "API Key first 10 chars: ${apiKey.take(10)}...")

            // Build request dengan system + user prompt
            val request = GeminiRequest(
                contents = listOf(
                    // System prompt sebagai context
                    Content(
                        role = "user",
                        parts = listOf(Part(text = SYSTEM_PROMPT))
                    ),
                    // User prompt
                    Content(
                        role = "user",
                        parts = listOf(Part(text = userPrompt))
                    )
                )
            )

            Log.d(TAG, "Sending request to Gemini API (gemini-2.0-flash)")

            // Send request ke Gemini API
            val response = apiService.generateContent(request, apiKey)

            Log.d(TAG, "Response received: ${response.candidates?.size ?: 0} candidates")

            // Extract dan return text response
            val result = GeminiResponseParser.extractText(response)
            Log.d(TAG, "Extracted result: $result")
            result

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "No error body"
            val errorMessage = buildErrorMessage(e.code(), errorBody)
            Log.e(TAG, errorMessage, e)
            errorMessage

        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}", e)
            "Error: ${e.message ?: "Unknown error occurred"}"
        }
    }

    private fun buildErrorMessage(code: Int, errorBody: String): String {
        return when (code) {
            401 -> "Error: Invalid API Key. Please check your GEMINI_API_KEY in local.properties"
            403 -> "Error: Access denied. Your API key may not have permission for Gemini models."
            404 -> "Error: Model not found. Using gemini-2.0-flash should work. Check Gemini API documentation."
            429 -> "Error: Too many requests. Please wait a moment before trying again."
            500 -> "Error: Gemini API server error. Please try again later."
            else -> "Error: $code - API Request Failed\nDetails: $errorBody"
        }
    }
}
