package com.example.reminds.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast
import com.example.common.base.model.AlarmNotificationEntity
import java.text.SimpleDateFormat
import java.util.*

const val INSERT_OBJECT_TIMER_DATA = 1 // insert or update
const val REMOVE_OBJECT_TIMER_DATA = 0

class NotificationService : Service() {
    private lateinit var mMessenger: Messenger

    /**
     * Handler of incoming messages from clients.
     */

    internal class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                REMOVE_OBJECT_TIMER_DATA -> {
                    val notify = msg.obj as AlarmNotificationEntity
                    cancelAlarm(notify.idContent.toInt())
                }
                INSERT_OBJECT_TIMER_DATA -> {
                    val notify = msg.obj as AlarmNotificationEntity
                    scheduleAlarm(notify.timeAlarm, notify.nameWork, notify.nameContent, notify.idContent.toInt())
                }
                else -> super.handleMessage(msg)
            }
        }

        private fun scheduleAlarm(
            scheduledTimeString: String?,
            title: String?,
            message: String?,
            idAlarm: Int
        ) {
            val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent =
                Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                    intent.putExtra(ScheduledWorker.NOTIFICATION_TITLE, title)
                    intent.putExtra(ScheduledWorker.NOTIFICATION_MESSAGE, message)
                    PendingIntent.getBroadcast(applicationContext, idAlarm, intent, 0)
                }

            val scheduledTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .parse(scheduledTimeString!!)

            scheduledTime?.let {
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP, scheduledTime.time, 24 * 60 * 60 * 1000, alarmIntent
                )
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
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */

    override fun onBind(intent: Intent): IBinder {
//        Toast.makeText(applicationContext, "binding", Toast.LENGTH_SHORT).show()
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }
}