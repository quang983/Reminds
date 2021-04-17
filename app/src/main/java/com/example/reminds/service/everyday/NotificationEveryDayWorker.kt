package com.example.reminds.service.everyday

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationEveryDayWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.d("workmanager", "doWork: work")
        return Result.success()
    }
}
