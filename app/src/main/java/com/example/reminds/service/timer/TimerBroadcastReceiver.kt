package com.example.reminds.service.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminds.service.timer.TimerService.Companion.NOTIFICATION_CREATE
import com.example.reminds.service.timer.TimerService.Companion.NOTIFICATION_PAUSE
import com.example.reminds.service.timer.TimerService.Companion.NOTIFICATION_RESTART
import com.example.reminds.service.timer.TimerService.Companion.NOTIFICATION_START
import com.example.reminds.service.timer.TimerService.Companion.NOTIFICATION_STATE
import com.example.reminds.service.timer.TimerService.Companion.NOTIFICATION_STOP

class TimerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (it.getStringExtra(NOTIFICATION_STATE)) {
                NOTIFICATION_CREATE -> {
                }
                NOTIFICATION_START -> {

                }
                NOTIFICATION_STOP -> {

                }
                NOTIFICATION_RESTART -> {

                }
                NOTIFICATION_PAUSE -> {

                }
            }
        }
    }
}