package com.example.quickescape.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import com.example.quickescape.data.model.Location
import com.example.quickescape.util.NavigationUtils
import java.util.Locale

/**
 * Tombol untuk membuka navigasi ke lokasi
 * Menampilkan icon navigasi dengan text "Arahkan Saya"
 */
@Composable
fun GoToLocationButton(
    context: Context,
    latitude: Double,
    longitude: Double,
    locationName: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isLoading) {
                NavigationUtils.openNavigationIntent(
                    context = context,
                    latitude = latitude,
                    longitude = longitude,
                    locationName = locationName
                )
            },
        color = Color(0xFFE8725E),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    Icons.Default.Navigation,
                    contentDescription = "Navigate",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                "Arahkan Saya",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GoToLocationIconButton(
    context: Context,
    latitude: Double,
    longitude: Double,
    locationName: String,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = {
            NavigationUtils.openNavigationIntent(
                context = context,
                latitude = latitude,
                longitude = longitude,
                locationName = locationName
            )
        },
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Surface(
            color = Color(0xFFE8725E),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Navigation,
                contentDescription = "Navigate to location",
                tint = Color.White,
                modifier = Modifier
                    .padding(10.dp)
                    .size(28.dp)
            )
        }
    }
}

@Composable
fun GoToLocationTab(
    location: Location,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            "Navigation",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Location Info Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            color = Color(0xFFFFF5F3),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Destination",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Text(
                    location.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
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
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFE8725E)
                    )
                    Text(
                        "${location.city}, ${location.island}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Text(
                    "Coordinates: ${String.format(Locale.US, "%.4f", location.location.latitude)}, ${String.format(Locale.US, "%.4f", location.location.longitude)}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Navigation Button
        GoToLocationButton(
            context = androidx.compose.ui.platform.LocalContext.current,
            latitude = location.location.latitude,
            longitude = location.location.longitude,
            locationName = location.name,
            modifier = Modifier.fillMaxWidth()
        )

        // Info Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            color = Color(0xFFF5F5F5),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp),
                        tint = Color(0xFFE8725E)
                    )
                    Text(
                        "Tap 'Arahkan Saya' to open Google Maps and start navigation",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
