package com.example.reminds.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.reminds.R

class NotificationWorker(val appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        notifyThis("Thong bao","Ha ha")
        return Result.success()
    }

    private fun notifyThis(title: String?, message: String?) {
        val b = NotificationCompat.Builder(appContext, "CHANNEL_ID")
        b.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_calendar)
            .setTicker("{your tiny message}")
            .setContentTitle(title)
            .setContentText(message)
            .setContentInfo("INFO")
        val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1, b.build())
    }

}