package com.example.reminds.ui.fragment.focus.home

import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.TimestampUtils

enum class STATE {
    RESUME, PAUSE, FINISH, INDIE
}

class FocusTodoHomeViewModel @ViewModelInject constructor() : BaseViewModel() {
    private val defaultTimer = 1500000L

    private var mCountDownTimer: CountDownTimer? = null

    var mTimerRunningState: MutableLiveData<STATE> = MutableLiveData(STATE.INDIE)

    var timerRunningStateLiveData: LiveData<STATE> = mTimerRunningState.switchMapLiveDataEmit {
        it
    }

    var mTimeLeftInMillis: Long = defaultTimer

    val timeShowLiveData: LiveData<String> = liveDataEmit {
        TimestampUtils.convertMiliTimeToTimeHourStr(mTimeLeftInMillis)
    }

    fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setTimerData(millisUntilFinished)
            }

            override fun onFinish() {
                mTimerRunningState.postValue(STATE.FINISH)
            }
        }.start()
        mTimerRunningState.postValue(STATE.RESUME)
    }

    fun pauseTimer() {
        mCountDownTimer?.cancel()
        mTimerRunningState.postValue(STATE.PAUSE)
    }

    fun setTimerData(timer: Long) {
        mTimeLeftInMillis = timer
        timeShowLiveData.postValue(TimestampUtils.convertMiliTimeToTimeHourStr(mTimeLeftInMillis))
    }

    fun resetState() {
        mCountDownTimer?.cancel()
        mTimerRunningState.postValue(STATE.INDIE)
        mTimeLeftInMillis = defaultTimer
        timeShowLiveData.checkDiffAndPostValue { TimestampUtils.convertMiliTimeToTimeHourStr(mTimeLeftInMillis) }

    }
}