package com.example.quickescape.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun ExploreScreen(
    nearbyLocations: List<Pair<Location, Float>>,
    isLoading: Boolean,
    userLocation: String = "Getting location...",
    onLocationClick: (Location) -> Unit,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                "Nearby Adventures",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "Discover destinations near you",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Location Info
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFFF5F5F5)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Your Location",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFE8725E)
                        )
                        Text(
                            userLocation,
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Update Location",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onRetryClick() },
                        tint = Color(0xFFE8725E)
                    )
                }
            }
        }

        HorizontalDivider()

        // Content
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE8725E),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Finding nearby destinations...",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (nearbyLocations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOff,
                        contentDescription = "No Locations",
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Text(
                        "No nearby destinations found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Try enabling location services or expanding the search radius",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Button(
                        onClick = onRetryClick,
                        modifier = Modifier.padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8725E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Retry",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Try Again")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(nearbyLocations) { (location, distance) ->
                    NearbyLocationCard(
                        location = location,
                        distance = distance,
                        onClick = { onLocationClick(location) }
                    )
                }
            }
        }
    }
}

@Composable
fun NearbyLocationCard(
    location: Location,
    distance: Float,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                var isLoading by remember { mutableStateOf(true) }
                var isError by remember { mutableStateOf(false) }

                AsyncImage(
                    model = location.image,
                    contentDescription = "Location Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onLoading = { isLoading = true },
                    onSuccess = { isLoading = false; isError = false },
                    onError = { isLoading = false; isError = true }
                )

                if (isLoading && !isError) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = Color(0xFFE8725E)
                        )
                    }
                }

                if (isError) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Error",
                            modifier = Modifier.size(48.dp),
                            tint = Color.LightGray
                        )
                    }
                }
            }

            // Info
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            location.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 20.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(12.dp),
                                tint = Color.Gray
                            )
                            Text(
                                "${location.city}, ${location.island}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Distance Badge
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        color = Color(0xFFFFF3E0)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Navigation,
                                contentDescription = "Distance",
                                modifier = Modifier.size(12.dp),
                                tint = Color(0xFFE8725E)
                            )
                            Text(
                                String.format(java.util.Locale.getDefault(), "%.1f km", distance),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE8725E),
                                modifier = Modifier.padding(start = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Rating and Category
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFE8725E)
                        )
                        Text(
                            "${location.rating}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            "(${location.ratingCount})",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            location.category,
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                // Price
                val priceFormatted = String.format(java.util.Locale.getDefault(), "%,d", location.price_start)
                Text(
                    "From Rp $priceFormatted",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE8725E),
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 1
                )
            }
        }
    }
}
