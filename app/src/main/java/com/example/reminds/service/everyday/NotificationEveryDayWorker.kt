package com.example.reminds.service.everyday

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.reminds.service.NotificationUtil


class NotificationEveryDayWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString(DAILY_NOTIFY_TITLE)
        val message = inputData.getString(DAILY_NOTIFY_MESSAGE)
        val idGroup = inputData.getLong(DAILY_NOTIFY_ID_GROUP, 1)

        NotificationUtil(applicationContext).showNotification(title.toString(), message.toString(), idGroup)
        return Result.success()
    }

    companion object {
        const val DAILY_NOTIFY_ID_GROUP = "DAILY_NOTIFY_ID_GROUP"
        const val DAILY_NOTIFY_TITLE = "DAILY_NOTIFY_TITLE"
        const val DAILY_NOTIFY_MESSAGE = "DAILY_NOTIFY_MESSAGE"
    }
}
