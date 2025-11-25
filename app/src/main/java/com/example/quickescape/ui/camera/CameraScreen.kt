package com.example.quickescape.ui.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    onPhotoTaken: (Uri) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    when {
        cameraPermissionState.status.isGranted -> {
            CameraView(
                onBackClick = onBackClick,
                onPhotoTaken = onPhotoTaken
            )
        }
        cameraPermissionState.status.shouldShowRationale -> {
            PermissionRationaleScreen(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                onBackClick = onBackClick
            )
        }
        else -> {
            PermissionDeniedScreen(
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun CameraView(
    onBackClick: () -> Unit,
    onPhotoTaken: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (capturedImageUri == null) {
            // Camera Preview
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewView = this

                        coroutineScope.launch {
                            val cameraProvider = getCameraProvider(ctx)

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(surfaceProvider)
                            }

                            val imageCaptureBuilder = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .setTargetRotation(display?.rotation ?: Surface.ROTATION_0)

                            imageCapture = imageCaptureBuilder.build()

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Camera Controls
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                // Top Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Transparent
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Capture Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .clickable(enabled = !isCapturing) {
                                isCapturing = true
                                imageCapture?.let { capture ->
                                    takePicture(
                                        context = context,
                                        imageCapture = capture,
                                        onImageSaved = { uri ->
                                            capturedImageUri = uri
                                            isCapturing = false
                                        },
                                        onError = {
                                            isCapturing = false
                                        }
                                    )
                                }
                            }
                            .background(
                                if (isCapturing) Color.Gray else Color.White.copy(alpha = 0.8f),
                                CircleShape
                            )
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            // Photo Preview
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = capturedImageUri,
                    contentDescription = "Captured photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Action Buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Retake
                        FloatingActionButton(
                            onClick = {
                                capturedImageUri = null
                            },
                            containerColor = Color.Red,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Retake",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Use Photo
                        FloatingActionButton(
                            onClick = {
                                capturedImageUri?.let { onPhotoTaken(it) }
                            },
                            containerColor = Color(0xFF4CAF50),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Use photo",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRationaleScreen(
    onRequestPermission: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "Camera",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Camera Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Please grant camera permission to take photos",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }

            TextButton(onClick = onBackClick) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun PermissionDeniedScreen(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "Camera permission denied",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBackClick) {
                Text("Go Back")
            }
        }
    }
}

private suspend fun getCameraProvider(context: Context): ProcessCameraProvider {
    return suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(context).also { future ->
            future.addListener({
                continuation.resume(future.get())
            }, ContextCompat.getMainExecutor(context))
        }
    }
}

private fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    onImageSaved: (Uri) -> Unit,
    onError: () -> Unit
) {
    val name = "IMG_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QuickEscape")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let { onImageSaved(it) }
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onError()
            }
        }
    )
}

