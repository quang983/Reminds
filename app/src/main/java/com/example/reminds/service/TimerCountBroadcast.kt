package com.example.reminds.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerCountBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action.equals("TIME_INFO")) {
            if (intent.hasExtra("VALUE")) {
            }
        }
    }

}