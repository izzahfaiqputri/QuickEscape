package com.example.quickescape.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quickescape.data.model.Location
import com.example.quickescape.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    savedLocations: List<Location>,
    isLoading: Boolean,
    onUpdateProfileImage: (Uri) -> Unit,
    onLocationClick: (Location) -> Unit,
    onRemoveSavedLocation: (String) -> Unit,
    onSaveProfileChanges: (String) -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userProfile?.name ?: currentUser?.displayName ?: "") }
    var imageUploading by remember { mutableStateOf(false) }

    // Update editedName ketika userProfile berubah
    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            editedName = userProfile.name.ifEmpty { currentUser?.displayName ?: "" }
            // Reset imageUploading flag ketika profile ter-update (artinya upload selesai)
            if (imageUploading && userProfile.profileImage.isNotEmpty()) {
                imageUploading = false
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUploading = true
            onUpdateProfileImage(it)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Profile Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8725E))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (userProfile?.profileImage?.isNotEmpty() == true) {
                        AsyncImage(
                            model = userProfile.profileImage,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }

                    // Edit button overlay
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color(0xFFE8725E),
                        shadowElevation = 4.dp
                    ) {
                        if (imageUploading || isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Photo",
                                modifier = Modifier
                                    .padding(6.dp)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name - dari Firebase User
                if (isEditingName) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                } else {
                    Text(
                        editedName.ifEmpty { currentUser?.displayName ?: "User Name" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Email - dari Firebase User (read-only)
                Text(
                    currentUser?.email ?: "user@example.com",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Edit/Save buttons
                if (isEditingName) {
                    Button(
                        onClick = {
                            isEditingName = false
                            onSaveProfileChanges(editedName)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFFE8725E),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save Changes", color = Color(0xFFE8725E), fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            isEditingName = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 8.dp),
                            tint = Color.White
                        )
                        Text("Edit Name", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Saved Locations Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Saved Destinations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "${savedLocations.size} destination${if (savedLocations.size != 1) "s" else ""}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Saved Locations List
        if (savedLocations.isEmpty()) {
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
                            Icons.Default.BookmarkBorder,
                            contentDescription = "No Saved",
                            modifier = Modifier.size(48.dp),
                            tint = Color.LightGray
                        )
                        Text(
                            "No saved destinations",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        } else {
            items(savedLocations) { location ->
                SavedLocationCard(
                    location = location,
                    onClick = { onLocationClick(location) },
                    onRemove = { onRemoveSavedLocation(location.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SavedLocationCard(
    location: Location,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image and Info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image
                AsyncImage(
                    model = location.image,
                    contentDescription = location.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        location.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
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
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFFE8725E)
                        )
                        Text(
                            String.format("%.1f", location.rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            // Remove Button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFFE8725E),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
