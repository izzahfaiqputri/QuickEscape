plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.quickescape"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.quickescape"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Add Firebase Analytics
    implementation(libs.firebase.analytics)

    // Firebase Authentication with KTX
    implementation(libs.firebase.auth.ktx)

    // Google Play Services Auth for Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Compose Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Firebase Firestore
    implementation(libs.firebase.firestore.ktx)

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // AndroidX Core untuk FileProvider
    implementation("androidx.core:core:1.10.1")

    // CameraX
    val camerax_version = "1.3.4"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}