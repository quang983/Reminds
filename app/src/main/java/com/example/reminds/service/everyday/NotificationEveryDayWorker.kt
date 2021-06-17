package com.example.reminds.service.everyday

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.common.base.model.daily.DailyDivideTaskDoneEntity
import com.example.domain.usecase.db.daily.done.InsertOrUpdateDailyTaskDoneUseCase
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.TimestampUtils.DATE_FORMAT_DEFAULT_WITHOUT_TIME
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class NotificationEveryDayWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    @Inject
    lateinit var doneTaskUseCase: InsertOrUpdateDailyTaskDoneUseCase

    override fun doWork(): Result {
        runBlocking {
            val title = inputData.getString(DAILY_NOTIFY_TITLE)
            val message = inputData.getString(DAILY_NOTIFY_MESSAGE)
            val idGroup = inputData.getLong(DAILY_NOTIFY_ID_GROUP, 1)
            val timeMillis = System.currentTimeMillis()
            val taskDone = DailyDivideTaskDoneEntity(TimestampUtils.getDate(timeMillis, DATE_FORMAT_DEFAULT_WITHOUT_TIME), idGroup, "", timeMillis)
            doneTaskUseCase.invoke(InsertOrUpdateDailyTaskDoneUseCase.Param(listOf(taskDone)))
            NotificationAction(applicationContext).showNotification(title.toString(), message.toString(), idGroup)
        }

        return Result.success()
    }

    companion object {
        const val DAILY_NOTIFY_ID_GROUP = "DAILY_NOTIFY_ID_GROUP"
        const val DAILY_NOTIFY_TITLE = "DAILY_NOTIFY_TITLE"
        const val DAILY_NOTIFY_MESSAGE = "DAILY_NOTIFY_MESSAGE"
    }
}
