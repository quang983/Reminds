package com.example.reminds.service

import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.common.base.model.AlarmNotificationEntity
import com.example.reminds.R
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.utils.TimestampUtils
import java.text.SimpleDateFormat
import java.util.*


const val INSERT_OBJECT_TIMER_DATA = 1 // insert or update
const val REMOVE_OBJECT_TIMER_DATA = 0

class NotificationService : Service() {
    private lateinit var mMessenger: Messenger
    lateinit var manager: ClipboardManager

    override fun onCreate() {
        super.onCreate()
        startForeground(this)
    }

    private fun startForeground(service: Service) {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(service, service.applicationContext.getString(R.string.notify_channel), "My Background Service")
            } else {
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(service, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        service.startForeground(101, notification)
        manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.addPrimaryClipChangedListener {
            Log.d("AppLog", manager.text?.toString() ?: "")
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String
    ): String {
        val chan =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val service =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)

        return channelId
    }

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
                    scheduleAlarm(TimestampUtils.getFullFormatTime(notify.timeAlarm, "dd/MM/yyyy HH:mm"), notify.nameWork, notify.nameContent, notify.idContent.toInt())
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