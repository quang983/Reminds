package com.example.reminds.service.timer

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.reminds.R

enum class TimerHelloState { STOPPED, PAUSED, RUNNING, TERMINATED }

class HelloService : Service() {

    companion object {
        var state = TimerState.TERMINATED
    }

    private lateinit var timer: CountDownTimer

    private val foreGroundId = 55
    private var secondsRemaining: Long = 0
    private var setTime: Long = 0
    private lateinit var showTime: String

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground()
    }

    private fun startForeground() {
        val channelId = "${applicationContext.packageName}.timer"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(foreGroundId, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                "PLAY" -> {
                    playTimer(
                        intent.getLongExtra("setTime", 0L),
                        intent.getBooleanExtra("forReplay", false)
                    )
                }
                "PAUSE" -> pauseTimer()
                "STOP" -> stopTimer()
                "TERMINATE" -> terminateTimer()
            }
        }
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.TERMINATED
        }
        NotificationTimer.removeNotification()
        stopSelf()
    }

    private fun playTimer(setTime: Long, isReplay: Boolean) {

        if (!isReplay) {
            this.setTime = setTime
            secondsRemaining = setTime
            startForeground(foreGroundId, NotificationTimer.createNotification(this, setTime))
        }

        timer = object : CountDownTimer(secondsRemaining, 1000) {
            override fun onFinish() {
                state = TimerState.STOPPED
                val minutesUntilFinished = setTime / 1000 / 60
                val secondsInMinuteUntilFinished = ((setTime / 1000) - minutesUntilFinished * 60)
                val secondsStr = secondsInMinuteUntilFinished.toString()
                val showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
                NotificationTimer.updateStopState(this@HelloService, showTime, true)
            }

            override fun onTick(millisUntilFinished: Long) {
                NotificationTimer.updateUntilFinished(millisUntilFinished + (1000 - (millisUntilFinished % 1000)) - 1000)
                secondsRemaining = millisUntilFinished
                updateCountdownUI()
            }
        }.start()

        state = TimerState.RUNNING
    }

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
            NotificationTimer.updateStopState(this@HelloService, showTime)
        }
    }

    private fun terminateTimer() {
        if (::timer.isInitialized) {
            timer.cancel()
            state = TimerState.TERMINATED
            NotificationTimer.removeNotification()
            stopSelf()
        }
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = (secondsRemaining / 1000) / 60
        val secondsInMinuteUntilFinished = ((secondsRemaining / 1000) - minutesUntilFinished * 60)
        val secondsStr = secondsInMinuteUntilFinished.toString()
        showTime = "$minutesUntilFinished : ${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"

        NotificationTimer.updateTimeLeft(this, showTime)
    }

}