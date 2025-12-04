package com.example.quickescape.network.gemini

import com.google.gson.annotations.SerializedName


data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val role: String = "user",
    val parts: List<Part>
)

data class Part(
    val text: String
)


data class GeminiResponse(
    val candidates: List<Candidate>?,
    @SerializedName("promptFeedback")
    val promptFeedback: PromptFeedback?
)

data class Candidate(
    val content: ContentResponse?,
    @SerializedName("finishReason")
    val finishReason: String?,
    val index: Int?
)

data class ContentResponse(
    val role: String?,
    val parts: List<PartResponse>?
)

data class PartResponse(
    val text: String?
)

data class PromptFeedback(
    @SerializedName("blockReason")
    val blockReason: String?,
    @SerializedName("safetyRatings")
    val safetyRatings: List<SafetyRating>?
)

data class SafetyRating(
    val category: String?,
    val probability: String?
)

object GeminiResponseParser {
    fun extractText(response: GeminiResponse): String {
        return response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: "No response from AI"
    }
}

