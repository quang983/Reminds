package com.example.reminds.ui.fragment.focus.home

import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.TimestampUtils


class FocusTodoHomeViewModel @ViewModelInject constructor() : BaseViewModel() {
    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mTimeLeftInMillis: Long = 200000

    val timeShowLiveData: LiveData<String> = MutableLiveData()
    val stateCalTime: LiveData<Boolean> = MutableLiveData()

    fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                updateButtons()
            }
        }.start()
        mTimerRunning = true
    }

    private fun updateCountDownText() {
        timeShowLiveData.postValue(TimestampUtils.convertMiliTimeToTimeHourStr(mTimeLeftInMillis))
    }

    private fun updateButtons() {
        stateCalTime.postValue(mTimerRunning)
    }
}