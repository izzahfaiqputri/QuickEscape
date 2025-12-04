package com.example.quickescape.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickescape.data.viewmodel.TravelAIViewModel

/**
 * Screen untuk Ask AI - Travel Assistant
 * Memungkinkan user untuk bertanya tentang travel destinations
 */
@Composable
fun AskAIScreen(
    viewModel: TravelAIViewModel = viewModel()
) {
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val conversationHistory by viewModel.conversationHistory.collectAsState()

    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        AskAIHeader(
            onClearClick = {
                viewModel.clearHistory()
                userInput = ""
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Chat History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Show welcome message if no history
            if (conversationHistory.isEmpty()) {
                item {
                    WelcomeCard()
                }
            }

            // Show conversation history (includes both user and assistant messages)
            items(conversationHistory) { message ->
                ChatBubble(message = message)
            }

            // Show loading indicator
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color(0xFFE8725E)
                        )
                    }
                }
            }

            // Show error message
            if (errorMessage.isNotEmpty()) {
                item {
                    ErrorCard(errorMessage = errorMessage)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Area
        AskAIInputField(
            userInput = userInput,
            onInputChange = { userInput = it },
            onSendClick = {
                if (userInput.isNotBlank()) {
                    viewModel.askAI(userInput)
                    userInput = ""
                }
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AskAIHeader(
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFFE8725E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Travel AI Assistant",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Text(
                "Ask anything about travel destinations",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 32.dp, top = 4.dp)
            )
        }

        IconButton(
            onClick = onClearClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Clear",
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun ChatBubble(
    message: TravelAIViewModel.ChatMessage
) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = if (isUser) Color(0xFFE8725E) else Color(0xFFF0F0F0),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (isUser) Color.White else Color.Black,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTime(message.timestamp),
                    fontSize = 10.sp,
                    color = if (isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun WelcomeCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = Color(0xFFFFF5F3),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = "Welcome",
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFE8725E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Welcome to Travel AI Assistant!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Ask me anything about travel destinations, tourism recommendations, nearby places, or travel tips.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Example questions
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExampleQuestionChip("Recommend me tourist attractions near Malang")
                ExampleQuestionChip("Give me good travel destinations in West Java")
                ExampleQuestionChip("What are the best beaches in East Java?")
            }
        }
    }
}

@Composable
private fun ExampleQuestionChip(question: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Text(
            text = "• $question",
            fontSize = 12.sp,
            color = Color(0xFFE8725E),
            modifier = Modifier.padding(12.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ErrorCard(errorMessage: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        color = Color(0xFFFFEBEE),
        shadowElevation = 1.dp
    ) {
        Text(
            text = errorMessage,
            fontSize = 12.sp,
            color = Color(0xFFC62828),
            modifier = Modifier.padding(12.dp),
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun AskAIInputField(
    userInput: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = userInput,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = {
                Text("Ask about travel destinations...", fontSize = 13.sp)
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            enabled = !isLoading,
            singleLine = false,
            maxLines = 3
        )

        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFFE8725E),
            shadowElevation = 2.dp
        ) {
            IconButton(
                onClick = onSendClick,
                enabled = !isLoading && userInput.isNotBlank(),
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60000 -> "just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}
