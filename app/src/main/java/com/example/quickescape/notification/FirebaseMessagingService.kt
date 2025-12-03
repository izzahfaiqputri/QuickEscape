package com.example.quickescape.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.quickescape.MainActivity
import com.example.quickescape.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token: $token")

        // Save token to Firestore untuk user ini
        val sharedPref = getSharedPreferences("QuickEscape", Context.MODE_PRIVATE)
        sharedPref.edit().putString("fcm_token", token).apply()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "Message received from: ${message.from}")

        // Check if message contains a notification payload
        message.notification?.let { notification ->
            val title = notification.title ?: "QuickEscape"
            val body = notification.body ?: ""

            sendNotification(title, body)
        }

        // Check if message contains a data payload
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data: ${message.data}")

            val title = message.data["title"] ?: "QuickEscape"
            val body = message.data["body"] ?: ""
            val locationId = message.data["locationId"]

            sendNotification(title, body, locationId)
        }
    }

    private fun sendNotification(title: String, body: String, locationId: String? = null) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            locationId?.let {
                putExtra("locationId", it)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "quick_escape_nearby_locations"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nearby Destinations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for nearby tourist destinations"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    companion object {
        private const val TAG = "FCMService"
    }
}

