package com.example.schoolsmart.notifications

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
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

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

// Checks permission and sends a notification
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

// Schedules a reminder 1 day before the due date of a task
fun scheduleReminder(context: Context, taskID: String, taskTitle: String, dueDate: Long){

    val oneDaysTime = 24L * 60 * 60 * 1000
    val reminderTime = dueDate - oneDaysTime
    val delay = reminderTime - System.currentTimeMillis()

    if(delay <= 0){
        return
    }

    val data = workDataOf(
        "title" to "Task Reminder",
        "text"  to "$taskTitle is due tomorrow."
    )

    val work = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(taskID, ExistingWorkPolicy.REPLACE, work)
}