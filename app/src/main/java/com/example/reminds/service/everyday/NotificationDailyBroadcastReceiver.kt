package com.example.reminds.service.everyday

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.reminds.service.ScheduledWorker

class NotificationDailyBroadcastReceiver  : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {

            val title = it.getStringExtra(ScheduledWorker.NOTIFICATION_TITLE)
            val message = it.getStringExtra(ScheduledWorker.NOTIFICATION_MESSAGE)
            val idTopic = it.getLongExtra(ScheduledWorker.TOPIC_ID_OPEN, 1)

            // Create Notification Data
            val notificationData = Data.Builder()
                .putString(ScheduledWorker.NOTIFICATION_TITLE, title)
                .putString(ScheduledWorker.NOTIFICATION_MESSAGE, message)
                .putLong(ScheduledWorker.TOPIC_ID_OPEN, idTopic)
                .build()

            // Init Worker
            val work = OneTimeWorkRequest.Builder(NotificationEveryDayWorker::class.java)
                .setInputData(notificationData)
                .build()

            // Start Worker
            context?.let { it1 -> WorkManager.getInstance(it1).beginWith(work).enqueue() }

            Log.d(javaClass.name, "WorkManager is Enqueued.")
        }
    }
}