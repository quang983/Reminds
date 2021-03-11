package com.example.reminds.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_DATE_CHANGED -> {
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                intent.let {
                    val title = it.getStringExtra(ScheduledWorker.NOTIFICATION_TITLE)
                    val message = it.getStringExtra(ScheduledWorker.NOTIFICATION_MESSAGE)

                    // Create Notification Data
                    val notificationData = Data.Builder()
                        .putString(ScheduledWorker.NOTIFICATION_TITLE, title)
                        .putString(ScheduledWorker.NOTIFICATION_MESSAGE, message)
                        .build()

                    // Init Worker
                    val work = OneTimeWorkRequest.Builder(ScheduledWorker::class.java)
                        .setInputData(notificationData)
                        .build()

                    // Start Worker
                    context?.let { it1 -> WorkManager.getInstance(it1).beginWith(work).enqueue() }

                    Log.d(javaClass.name, "WorkManager is Enqueued.")
                }
            }
        }
    }
}