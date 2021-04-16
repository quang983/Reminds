package com.example.reminds.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import com.example.common.base.model.AlarmNotificationEntity
import com.example.reminds.service.ScheduledWorker.Companion.NOTIFICATION_MESSAGE
import com.example.reminds.service.ScheduledWorker.Companion.NOTIFICATION_TITLE
import com.example.reminds.service.ScheduledWorker.Companion.TOPIC_ID_OPEN
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.TimestampUtils.DATE_FORMAT_DEFAULT
import java.text.SimpleDateFormat
import java.util.*


const val INSERT_OBJECT_TIMER_DATA = 1 // insert or update
const val REMOVE_OBJECT_TIMER_DATA = 0

open class NotificationService : Service() {
    private lateinit var mMessenger: Messenger

    internal class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                REMOVE_OBJECT_TIMER_DATA -> {
                    val notify = msg.obj as? AlarmNotificationEntity
                    notify?.let {
                        cancelAlarm(notify.idContent.toInt())
                    }
                }
                INSERT_OBJECT_TIMER_DATA -> {
                    val notify = msg.obj as? AlarmNotificationEntity
                    notify?.let {
                        scheduleAlarm(
                            TimestampUtils.getFullFormatTime(notify.timeAlarm, DATE_FORMAT_DEFAULT),
                            notify, notify.idContent.toInt()
                        )
                    }
                }
                else -> super.handleMessage(msg)
            }
        }

        private fun scheduleAlarm(
            scheduledTimeString: String?,
            item: AlarmNotificationEntity,
            idAlarm: Int
        ) {
            val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent =
                Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                    intent.putExtra(NOTIFICATION_TITLE, item.nameWork)
                    intent.putExtra(NOTIFICATION_MESSAGE, item.nameContent)
                    intent.putExtra(TOPIC_ID_OPEN, item.idTopic)
                    PendingIntent.getBroadcast(applicationContext, idAlarm, intent, 0)
                }

            val scheduledTime = SimpleDateFormat(DATE_FORMAT_DEFAULT, Locale.getDefault())
                .parse(scheduledTimeString!!)

            scheduledTime?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTime.time, alarmIntent)
                else alarmMgr.setExact(AlarmManager.RTC_WAKEUP, scheduledTime.time, alarmIntent)
            }
        }

        private fun cancelAlarm(idAlarm: Int) {
            val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                val pendingIntent = PendingIntent.getBroadcast(applicationContext, idAlarm, intent, 0)
                alarmMgr.cancel(pendingIntent)
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }
}