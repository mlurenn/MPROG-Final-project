package com.example.schoolsmart.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result{
        val title = inputData.getString("title") ?: "Task Reminder"
        val text  = inputData.getString("text") ?: ""

        sendNotification(applicationContext, title, text)

        return Result.success()
    }
}