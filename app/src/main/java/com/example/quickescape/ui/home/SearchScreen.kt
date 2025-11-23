package com.example.quickescape.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickescape.data.model.Location

@Composable
fun SearchScreen(
    locations: List<Location>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit,
    onLocationClick: (Location) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Location>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    // Filter results berdasarkan searchQuery
    fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            searchResults = locations.filter { location ->
                location.name.contains(query, ignoreCase = true) ||
                location.city.contains(query, ignoreCase = true) ||
                location.island.contains(query, ignoreCase = true) ||
                location.category.contains(query, ignoreCase = true)
            }
        } else {
            searchResults = emptyList()
        }
    }

    // Update hasil pencarian ketika user mengetik
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
            onSearch(searchQuery)
            // Lakukan filtering local
            performSearch(searchQuery)
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }

    // Update hasil ketika locations berubah
    LaunchedEffect(locations) {
        if (searchQuery.isNotEmpty()) {
            performSearch(searchQuery)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header dengan Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFFF5F5F5)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        searchQuery = newQuery
                    },
                    placeholder = { Text("Search destination...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color(0xFFE8725E)
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    searchResults = emptyList()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    singleLine = true
                )
            }
        }

        HorizontalDivider()

        // Search Results
        if (searchQuery.isEmpty()) {
            // Empty state - sebelum user mulai mencari
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Text(
                        "Start searching",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        "Find your next adventure",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            // Menampilkan hasil pencarian
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (isSearching || isLoading) {
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
                } else if (searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    contentDescription = "No results",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.LightGray
                                )
                                Text(
                                    "No results found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                                Text(
                                    "Try searching with different keywords",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(searchResults) { location ->
                        LocationCard(
                            location = location,
                            onClick = { onLocationClick(location) }
                        )
                    }
                }
            }
        }
    }
}
