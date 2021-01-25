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
import com.example.common.base.model.ContentDataEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

const val MSG_SAY_HELLO = 1

class NotificationService : Service() {
    private lateinit var mMessenger: Messenger
    private val timeList: HashMap<String, HashMap<String, ContentDataEntity>> = HashMap()

    /**
     * Handler of incoming messages from clients.
     */

    internal class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SAY_HELLO -> {
                }
                else -> super.handleMessage(msg)
            }
        }

        var i = 0

        private fun scheduleAlarm(
            scheduledTimeString: String?,
            title: String?,
            message: String?
        ) {
            val alarmMgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent =
                Intent(applicationContext, NotificationBroadcastReceiver::class.java).let { intent ->
                    intent.putExtra(ScheduledWorker.NOTIFICATION_TITLE, title)
                    intent.putExtra(ScheduledWorker.NOTIFICATION_MESSAGE, message)
                    PendingIntent.getBroadcast(applicationContext, i++, intent, 0)
                }

            // Parse Schedule time
            val scheduledTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(scheduledTimeString!!)

            scheduledTime?.let {
                // With set(), it'll set non repeating one time alarm.
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP, scheduledTime.time, 24 * 60 * 60 * 1000, alarmIntent
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }
/*
    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                scheduleAlarm("2020-01-25 17:21:00", "Notifications", "this is notification at time1")
                scheduleAlarm("2020-01-25 17:16:00", "Notifications", "this is notification at time2")
                scheduleAlarm("2020-01-25 17:17:00", "Notifications", "this is notification at time3")
                scheduleAlarm("2020-01-25 17:18:00", "Notifications", "this is notification at time4")
                scheduleAlarm("2020-01-25 17:19:00", "Notifications", "this is notification at time5")
                scheduleAlarm("2020-01-25 17:20:00", "Notifications", "this is notification at time6")
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
        }
    }*/

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */

    override fun onBind(intent: Intent): IBinder {
        Toast.makeText(applicationContext, "binding", Toast.LENGTH_SHORT).show()
        mMessenger = Messenger(IncomingHandler(this))
        return mMessenger.binder
    }
}