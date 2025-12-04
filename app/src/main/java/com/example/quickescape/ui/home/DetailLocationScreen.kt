package com.example.quickescape.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import com.example.quickescape.data.model.Location
import com.example.quickescape.data.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DetailLocationScreen(
    location: Location,
    reviews: List<Review>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onAddReviewClick: () -> Unit,
    onSaveClick: (String) -> Unit,
    onDeleteReview: (String) -> Unit,
    isSaved: Boolean = false,
    photos: List<String> = emptyList(),
    onAddPhotoClick: () -> Unit = {},
    onDeletePhoto: (String) -> Unit = {}, // Add delete photo functionality
    isUploadingPhoto: Boolean = false,
    foods: List<com.example.quickescape.data.model.Food> = emptyList(),
    onOrderFood: (com.example.quickescape.data.model.Food, Int) -> Unit = { _, _ -> }
) {
    var saved by remember { mutableStateOf(isSaved) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Reviews", "Photos", "Food")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = location.image,
                    contentDescription = location.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onBackClick() },
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onBackClick() },
                        tint = Color.Black
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            saved = !saved
                            onSaveClick(location.id)
                        },
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (saved) "Unsave" else "Save",
                        modifier = Modifier.padding(8.dp),
                        tint = if (saved) Color(0xFFE8725E) else Color(0xFFE8725E)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    location.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFFE8725E)
                    )
                    Text(
                        "${location.city}, ${location.island}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingStars(rating = location.rating, size = 20.dp)
                        Text(
                            String.format("%.1f", location.rating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            "(${location.ratingCount} reviews)",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Text(
                        "Rp ${String.format("%,d", location.price_start)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE8725E)
                    )
                }

                Surface(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = Color(0xFFF0F0F0)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            when (location.category) {
                                "Mount" -> Icons.Default.Terrain
                                "Beach" -> Icons.Default.Water
                                "Crater" -> Icons.Default.Terrain
                                "Waterfall" -> Icons.Default.Water
                                "Forest" -> Icons.Default.Forest
                                "Temple" -> Icons.Default.Apartment
                                "Museum" -> Icons.Default.Museum
                                "Park" -> Icons.Default.Park
                                else -> Icons.Default.Place
                            },
                            contentDescription = location.category,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFE8725E)
                        )
                        Text(
                            location.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }

        // Tab Row
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White,
                contentColor = Color(0xFFE8725E),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }
        }

        item {
            HorizontalDivider()
        }

        // Tab Content
        when (selectedTab) {
            0 -> { // Reviews
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Reviews (${reviews.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Button(
                            onClick = onAddReviewClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE8725E)
                            ),
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Review",
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                            Text("Add Review", fontSize = 12.sp)
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                if (reviews.isEmpty() && !isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No reviews yet. Be the first to review!",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                if (reviews.isNotEmpty()) {
                    items(reviews) { review ->
                        ReviewCard(
                            review = review,
                            onDeleteClick = { onDeleteReview(review.id) }
                        )
                    }
                }
            }
            1 -> { // Photos
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Photos (${photos.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Button(
                                onClick = onAddPhotoClick,
                                enabled = !isUploadingPhoto,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE8725E)
                                ),
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                if (isUploadingPhoto) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Add Photo",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Photo", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (photos.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.PhotoLibrary,
                                        contentDescription = "No photos",
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "No photos yet. Be the first to add!",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            // Display photos in a simple column (no nested LazyColumn)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                photos.forEach { photoUrl ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFFAFAFA))
                                            .clickable { /* TODO: View photo in full screen */ }
                                    ) {
                                        AsyncImage(
                                            model = photoUrl,
                                            contentDescription = "Location photo",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp),
                                            contentScale = ContentScale.Crop
                                        )

                                        // Delete icon
                                        Surface(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(50))
                                                .clickable {
                                                    onDeletePhoto(photoUrl)
                                                },
                                            color = Color.White,
                                            shadowElevation = 4.dp
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Photo",
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> { // Food
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Food & Beverages",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (foods.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No food available for this location.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            // Use forEach instead of items() since we're inside an item {} block
                            foods.forEach { food ->
                                FoodItem(
                                    food = food,
                                    onOrderClick = { quantity ->
                                        onOrderFood(food, quantity)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun RatingStars(rating: Float, size: androidx.compose.ui.unit.Dp = 16.dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                modifier = Modifier.size(size),
                tint = if (index < rating.toInt()) Color(0xFFE8725E) else Color.LightGray
            )
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = Color(0xFFFAFAFA),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color.LightGray
                    ) {
                        if (review.userImage.isNotEmpty()) {
                            AsyncImage(
                                model = review.userImage,
                                contentDescription = review.userName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = review.userName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            review.userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            formatTimestamp(review.timestamp),
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingStars(rating = review.rating, size = 14.dp)
                Text(
                    String.format("%.1f", review.rating),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            if (review.comment.isNotEmpty()) {
                Text(
                    review.comment,
                    fontSize = 13.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp),
                    lineHeight = 18.sp
                )
            }

            if (review.photos.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(review.photos) { photoUrl ->
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Review photo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItem(
    food: com.example.quickescape.data.model.Food,
    onOrderClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = Color(0xFFFAFAFA),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = food.image,
                contentDescription = food.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rp ${String.format("%,d", food.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE8725E)
                )

                Text(
                    text = food.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var quantity by remember { mutableStateOf(1) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (quantity > 1) {
                                quantity--
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.width(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    IconButton(
                        onClick = {
                            quantity++
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = { onOrderClick(quantity) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8725E)
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .widthIn(min = 80.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        Icons.Default.AddShoppingCart,
                        contentDescription = "Order",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Order", fontSize = 11.sp)
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
