package com.example.schoolsmart.ui.components

import android.Manifest
import android.R
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

fun notificationSetup(activity: Activity){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channel = NotificationChannel(
            "CHANNEL_ID",
            "Channel",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = activity.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

fun sendNotification(context: Context, title: String, text: String){

    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ActivityCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED) {

        val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)

        with(NotificationManagerCompat.from(context)){
            notify(1, builder.build())
        }
    }
}