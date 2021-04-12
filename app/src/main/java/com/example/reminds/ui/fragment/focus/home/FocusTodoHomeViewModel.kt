package com.example.reminds.ui.fragment.focus.home

import android.os.CountDownTimer
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reminds.common.BaseViewModel
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.getOrDefault
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class FocusTodoHomeViewModel @ViewModelInject constructor() : BaseViewModel() {
    private var mCountDownTimer: CountDownTimer? = null

    private var mTimerRunning = false

    var mTimeLeftInMillis: MutableLiveData<Long> = MutableLiveData()

    val timeShowLiveData: LiveData<String> = mTimeLeftInMillis.switchMapLiveDataEmit {
        TimestampUtils.convertMiliTimeToTimeHourStr(it)
    }

    val stateCalTime: LiveData<Boolean> = MutableLiveData()

    init {
        Log.d("tickist", "create new item ")
    }

    fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis.getOrDefault(10000), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                mTimerRunning = false
                updateButtons()
            }
        }.start()
        mTimerRunning = true
    }

    private fun updateButtons() {
        stateCalTime.postValue(mTimerRunning)
    }
}