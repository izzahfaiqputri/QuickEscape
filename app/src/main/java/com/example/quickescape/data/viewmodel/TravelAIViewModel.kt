package com.example.quickescape.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickescape.network.gemini.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TravelAIViewModel : ViewModel() {
    private val repository = GeminiRepository()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _conversationHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val conversationHistory: StateFlow<List<ChatMessage>> = _conversationHistory.asStateFlow()

    /**
     * Mengirim pertanyaan ke AI dan mendapatkan response
     *
     * @param userPrompt User input pertanyaan tentang travel
     */
    fun askAI(userPrompt: String) {
        // Validasi input
        if (userPrompt.isBlank()) {
            _errorMessage.value = "Please enter a question"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = ""
                _aiResponse.value = "" // Clear previous response

                val response = repository.askGemini(userPrompt)

                // Add to conversation history (source of truth)
                val updatedHistory = _conversationHistory.value.toMutableList().apply {
                    add(ChatMessage(
                        role = "user",
                        text = userPrompt,
                        timestamp = System.currentTimeMillis()
                    ))
                    add(ChatMessage(
                        role = "assistant",
                        text = response,
                        timestamp = System.currentTimeMillis()
                    ))
                }
                _conversationHistory.value = updatedHistory

            } catch (e: Exception) {
                // Handle error
                _errorMessage.value = "Error: ${e.message ?: "Unknown error"}"
            } finally {
                // Set loading state to false
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear conversation history
     */
    fun clearHistory() {
        _conversationHistory.value = emptyList()
        _aiResponse.value = ""
        _errorMessage.value = ""
    }

    /**
     * Data class untuk chat message
     */
    data class ChatMessage(
        val role: String, // "user" or "assistant"
        val text: String,
        val timestamp: Long
    )
}
