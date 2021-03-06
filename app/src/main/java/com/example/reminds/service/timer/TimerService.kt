package com.example.reminds.service.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.example.reminds.R

enum class TimerState { STOPPED, PAUSED, RUNNING, TERMINATED }

class TimerService : JobIntentService() {

    private lateinit var timer: CountDownTimer

    private var secondsRemaining: Long = 0
    private var setTime: Long = 0
    private lateinit var showTime: String

    companion object {
        var state = TimerState.TERMINATED
        const val NOTIFICATION_STATE = "NOTIFICATION_STATE"
        const val NOTIFICATION_START = "NOTIFICATION_START"
        const val NOTIFICATION_STOP = "NOTIFICATION_STOP"
        const val NOTIFICATION_RESTART = "NOTIFICATION_RESTART"
        const val NOTIFICATION_PAUSE = "NOTIFICATION_PAUSE"
        const val NOTIFICATION_CREATE = "NOTIFICATION_CREATE"

        const val NOTIFICATION_TIMER = "NOTIFICATION_TIMER"

        private const val UNIQUE_JOB_ID = 42

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, TimerService::class.java, UNIQUE_JOB_ID, intent)
        }
    }

    private val foreGroundId = 55

    override fun onHandleWork(intent: Intent) {
        intent.apply {
            val timer = getLongExtra(NOTIFICATION_TIMER, 0L)
            when (action) {
                NOTIFICATION_CREATE -> {
                    createNotification(timer)
                }
                NOTIFICATION_START -> {
//                    playTimer(timer)
                }
            }
        }
    }

    private fun createNotification(timer: Long) {
        this.setTime = setTime
        secondsRemaining = setTime
        NotificationTimer.createNotification(this, timer)
    }


    override fun onCreate() {
        super.onCreate()
        startForeground()
    }

    private fun startForeground() {
        val channelId = "${applicationContext.packageName}.timerr"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(foreGroundId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
/*
    fun onTaskRemoved(rootIntent: Intent?) {
        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.TERMINATED
        }
        NotificationTimer.removeNotification()
        applicationContext.stopService(rootIntent)
    }*/

    private fun pauseTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.PAUSED
            NotificationTimer.updatePauseState(this, showTime)
        }
    }

    private fun stopTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.STOPPED
            val minutesUntilFinished = setTime / 1000 / 60
            val secondsInMinuteUntilFinished = ((setTime / 1000) - minutesUntilFinished * 60)
            val secondsStr = secondsInMinuteUntilFinished.toString()
            val showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
            NotificationTimer.updateStopState(this, showTime)
        }
    }

    private fun terminateTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.TERMINATED
            NotificationTimer.removeNotification()
//                stopservice()
        }
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = (secondsRemaining / 1000) / 60
        val secondsInMinuteUntilFinished = ((secondsRemaining / 1000) - minutesUntilFinished * 60)
        val secondsStr = secondsInMinuteUntilFinished.toString()
        showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"

        NotificationTimer.updateTimeLeft(this, showTime)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
}
