package com.example.quickescape.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.quickescape.MainActivity
import com.example.quickescape.R
import com.example.quickescape.data.model.Location

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Nearby Destinations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when you're near a tourist destination"
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNearbyLocationNotification(userName: String, location: Location, distance: Float) {
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "Notification permission not granted")
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("locationId", location.id)
            putExtra("navigate_to_detail", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            location.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val distanceText = if (distance < 1) {
            "${(distance * 1000).toInt()} meters"
        } else {
            String.format("%.1f km", distance)
        }

        val title = "Hi $userName! 👋"
        val message = "Kamu dekat dengan ${location.name} (${distanceText} dari lokasimu). Yuk explore sekarang!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        try {
            notificationManager.notify(location.id.hashCode(), notification)
            Log.d(TAG, "Notification sent for ${location.name}")

            // Save to SharedPreferences to prevent duplicate notifications
            saveNotifiedLocation(location.id)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to send notification: ${e.message}")
        }
    }

    fun sendWelcomeNotification(userName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val title = "Selamat Datang, $userName! 🎉"
        val message = "Siap menjelajahi destinasi wisata Indonesia? Aktifkan lokasi untuk mendapat notifikasi saat dekat dengan destinasi menarik!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(WELCOME_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to send welcome notification: ${e.message}")
        }
    }

    private fun saveNotifiedLocation(locationId: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notifiedLocations = sharedPref.getStringSet(KEY_NOTIFIED_LOCATIONS, mutableSetOf()) ?: mutableSetOf()
        val updated = notifiedLocations.toMutableSet()
        updated.add(locationId)
        sharedPref.edit().putStringSet(KEY_NOTIFIED_LOCATIONS, updated).apply()
    }

    fun hasNotifiedForLocation(locationId: String): Boolean {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notifiedLocations = sharedPref.getStringSet(KEY_NOTIFIED_LOCATIONS, mutableSetOf()) ?: mutableSetOf()
        return notifiedLocations.contains(locationId)
    }

    fun clearNotifiedLocations() {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putStringSet(KEY_NOTIFIED_LOCATIONS, mutableSetOf()).apply()
    }

    companion object {
        private const val TAG = "NotificationHelper"
        private const val CHANNEL_ID = "quick_escape_nearby"
        private const val WELCOME_NOTIFICATION_ID = 1000
        private const val PREFS_NAME = "QuickEscapeNotifications"
        private const val KEY_NOTIFIED_LOCATIONS = "notified_locations"
    }
}

