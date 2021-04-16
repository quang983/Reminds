package com.example.reminds.service.timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.example.reminds.R

enum class TimerHelloState { STOPPED, PAUSED, RUNNING, TERMINATED }

class HelloService : Service() {
    private lateinit var mMessenger: Messenger

    companion object {
        var state = TimerState.TERMINATED

        const val MESSAGE_INDIE_NOTIFICATION = 0
        const val MESSAGE_PLAY_NOTIFICATION = 1
        const val MESSAGE_PAUSE_NOTIFICATION = 2
        const val MESSAGE_CANCEL_NOTIFICATION = 3
    }

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private lateinit var timer: CountDownTimer

    private val foreGroundId = 55
    private var secondsRemaining: Long = 0
    private var setTime: Long = 0
    private lateinit var showTime: String

    override fun onBind(intent: Intent): IBinder? {
        mMessenger = Messenger(serviceHandler)
        return mMessenger.binder
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("hello_service", "My Notification Service")
            } else {
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

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
//        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
//        serviceHandler?.obtainMessage()?.also { msg ->
//            msg.arg1 = startId
//            serviceHandler?.sendMessage(msg)
//        }

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                when (msg.what) {
                    MESSAGE_INDIE_NOTIFICATION -> {
                    }
                    MESSAGE_PLAY_NOTIFICATION -> {
                        val timer = msg.obj as? Long ?: 0L
                        playTimer(timer, false)
//                        NotificationTimer.play(applicationContext, timer)
                    }
                    MESSAGE_PAUSE_NOTIFICATION -> {
                        pauseTimer()
                    }
                    MESSAGE_CANCEL_NOTIFICATION -> {
                        stopTimer()
                    }
                }
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
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
//            stopSelf()
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