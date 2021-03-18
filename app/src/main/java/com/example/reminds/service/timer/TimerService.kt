package com.example.reminds.service.timer

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.reminds.R
import com.example.reminds.service.NotificationService
import com.example.reminds.service.REMOVE_OBJECT_TIMER_DATA
import java.util.*
import java.util.concurrent.TimeUnit


enum class TimerState { STOPPED, PAUSED, RUNNING, TERMINATED }

class TimerService : Service() {
    private lateinit var mMessenger: Messenger

    companion object {
        var state = TimerState.TERMINATED
    }
    private val foreGroundId = 55

    override fun onBind(intent: Intent): IBinder {
        mMessenger = Messenger(NotificationService.IncomingHandler(this))
        return mMessenger.binder
    }

    override fun onCreate() {
        super.onCreate()
        startForeground()
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

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

    internal class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler() {

        private lateinit var timer: CountDownTimer

        private var secondsRemaining: Long = 0
        private var setTime: Long = 0
        private lateinit var showTime: String


        override fun handleMessage(msg: Message) {
            when (msg.what) {
                REMOVE_OBJECT_TIMER_DATA -> {
                    playTimer(
                        intent.getLongExtra("setTime", 0L),
                        intent.getBooleanExtra("forReplay", false)
                    )
                }
                else -> super.handleMessage(msg)
            }
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
                    NotificationTimer.updateStopState(this@TimerService, showTime, true)
                }

                override fun onTick(millisUntilFinished: Long) {
                    NotificationTimer.updateUntilFinished(millisUntilFinished + (1000 - (millisUntilFinished % 1000)) - 1000)
                    secondsRemaining = millisUntilFinished
                    updateCountdownUI()
                }
            }.start()

            state = TimerState.RUNNING
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
                NotificationTimer.updateStopState(this@TimerService, showTime)
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


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    inner class CounterClass(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        @SuppressLint("DefaultLocale")
        override fun onTick(millisUntilFinished: Long) {
            val hms = java.lang.String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            )
            println(hms)
            val timerInfoIntent = Intent("TIME_INFO")
            timerInfoIntent.putExtra("VALUE", hms)
            LocalBroadcastManager.getInstance(this@TimerService).sendBroadcast(timerInfoIntent)
        }

        override fun onFinish() {
            val timerInfoIntent = Intent("TIME_INFO")
            timerInfoIntent.putExtra("VALUE", "Completed")
            LocalBroadcastManager.getInstance(this@TimerService).sendBroadcast(timerInfoIntent)
        }
    }
}