package com.example.reminds.ui.fragment.focus.home

import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.getOrDefault

enum class STATE {
    START, PAUSE, CANCEL, INDIE
}

class FocusTodoHomeViewModel @ViewModelInject constructor() : BaseViewModel() {
    private var mCountDownTimer: CountDownTimer? = null

    var mTimerRunning = STATE.INDIE

    var mTimeLeftInMillis: MutableLiveData<Long> = MutableLiveData()

    val timeShowLiveData: LiveData<String> = mTimeLeftInMillis.switchMapLiveDataEmit {
        TimestampUtils.convertMiliTimeToTimeHourStr(it)
    }

    fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis.getOrDefault(10000), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                mTimerRunning = STATE.CANCEL
            }
        }.start()
        mTimerRunning = STATE.START
    }

    fun pauseTimer() {
        mCountDownTimer?.cancel()
        mTimerRunning = STATE.PAUSE
    }
}