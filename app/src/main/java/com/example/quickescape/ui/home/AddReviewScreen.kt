package com.example.quickescape.ui.home

import android.net.Uri
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.quickescape.data.model.Review
import java.io.File

@Composable
fun AddReviewScreen(
    locationName: String,
    onBackClick: () -> Unit,
    onSubmitClick: (Review, List<Uri>) -> Unit
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Text(
                        "Add Review",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            HorizontalDivider()

            Column(modifier = Modifier.padding(16.dp)) {
                // Location Info
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color(0xFFF5F5F5)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFFE8725E)
                        )
                        Text(
                            locationName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Rating Section
                Text(
                    "Your Rating",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star ${index + 1}",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = (index + 1).toFloat() }
                                .padding(4.dp),
                            tint = if (index < rating.toInt()) Color(0xFFE8725E) else Color.LightGray
                        )
                    }
                }

                Text(
                    if (rating > 0) String.format(java.util.Locale.getDefault(), "%.1f out of 5", rating) else "Select a rating",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )

                // User Name Section
                Text(
                    "Your Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 24.dp)
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    placeholder = { Text("Enter your name", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE8725E),
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Name",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                    }
                )

                // Comment Section
                Text(
                    "Your Comment",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp)
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = {
                        if (it.length <= 500) comment = it
                    },
                    placeholder = { Text("Share your experience...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE8725E),
                        unfocusedBorderColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Text(
                    "${comment.length}/500",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )

                // Extra padding at bottom for scroll
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Fixed Submit Button at Bottom
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFFE8725E),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = {
                    if (rating > 0 && userName.isNotBlank() && comment.isNotBlank()) {
                        isSubmitting = true
                        val review = Review(
                            userName = userName,
                            rating = rating,
                            comment = comment,
                            timestamp = System.currentTimeMillis(),
                            userImage = "",
                            photos = emptyList()
                        )
                        onSubmitClick(review, emptyList())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8725E),
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = rating > 0 && userName.isNotBlank() && comment.isNotBlank() && !isSubmitting
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        if (isSubmitting) "Submitting..." else "Submit Review",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
