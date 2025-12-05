package com.example.quickescape.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quickescape.data.model.Location
import com.example.quickescape.data.viewmodel.TravelAIViewModel

@Composable
fun AskAIScreen(
    viewModel: TravelAIViewModel,
    onLocationClick: (Location) -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val conversationHistory by viewModel.conversationHistory.collectAsState()

    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            // Show welcome message if no history
            if (conversationHistory.isEmpty()) {
                item {
                    WelcomeCard()
                }
            }

            // Show conversation history (includes both user and assistant messages)
            items(conversationHistory) { message ->
                ChatBubble(
                    message = message,
                    onLocationClick = onLocationClick
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Color(0xFFE8725E)
                            )
                            Text(
                                "Searching destinations...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    modifier = Modifier.size(22.dp),
                    tint = Color(0xFFE8725E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Travel AI Assistant",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                "Ask about nearby places, best ratings & more",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 30.dp, top = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    message: TravelAIViewModel.ChatMessage,
    onLocationClick: (Location) -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Message bubble
        Surface(
            modifier = Modifier
                .widthIn(max = if (isUser) 280.dp else 320.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = if (isUser) Color(0xFFE8725E) else Color(0xFFF0F0F0),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = if (isUser) Color.White else Color.Black,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatTime(message.timestamp),
                    fontSize = 9.sp,
                    color = if (isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Show location cards if assistant message has locations
        if (!isUser && message.locations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFFE8725E)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Found ${message.locations.size} destinations (tap to view):",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal scrollable location cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(message.locations) { locationWithDistance ->
                    AILocationCard(
                        locationWithDistance = locationWithDistance,
                        onClick = { onLocationClick(locationWithDistance.location) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AILocationCard(
    locationWithDistance: TravelAIViewModel.LocationWithDistance,
    onClick: () -> Unit
) {
    val location = locationWithDistance.location
    val distance = locationWithDistance.distanceKm

    Surface(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = location.image,
                    contentDescription = location.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Distance badge
                if (distance != Float.MAX_VALUE) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = Color(0xFFE8725E)
                    ) {
                        Text(
                            text = String.format("%.1f km", distance),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Info
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    location.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(10.dp),
                        tint = Color.Gray
                    )
                    Text(
                        "${location.city}",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFFE8725E)
                        )
                        Text(
                            "${location.rating}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    // Category chip
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            location.category,
                            fontSize = 8.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                }

                // Price
                val priceFormatted = String.format(java.util.Locale.getDefault(), "%,d", location.price_start)
                Text(
                    "From Rp $priceFormatted",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE8725E),
                    modifier = Modifier.padding(top = 6.dp)
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
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFE8725E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Welcome to Travel AI Assistant!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "I can help you find destinations based on your preferences. Try asking:",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExampleQuestionChip(
                    icon = Icons.Default.NearMe,
                    question = "Tempat wisata terdekat dari lokasi saya"
                )
                ExampleQuestionChip(
                    icon = Icons.Default.Star,
                    question = "Destinasi dengan rating terbaik"
                )
                ExampleQuestionChip(
                    icon = Icons.Default.BeachAccess,
                    question = "Pantai terbaik di Bali"
                )
                ExampleQuestionChip(
                    icon = Icons.Default.Terrain,
                    question = "Wisata alam di Jawa Timur"
                )
                ExampleQuestionChip(
                    icon = Icons.Default.Savings,
                    question = "Tempat wisata murah di Yogyakarta"
                )
            }
        }
    }
}

@Composable
private fun ExampleQuestionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    question: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFFE8725E)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = question,
                fontSize = 11.sp,
                color = Color(0xFFE8725E),
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
        }
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = userInput,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp, max = 120.dp)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = {
                Text(
                    "Cari tempat wisata...",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color(0xFFE8725E)
            ),
            enabled = !isLoading,
            singleLine = false,
            maxLines = 3
        )

        Surface(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(23.dp)),
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
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
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
