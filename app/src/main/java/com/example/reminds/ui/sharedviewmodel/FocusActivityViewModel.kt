package com.example.reminds.ui.sharedviewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.model.WorkDataEntity
import com.example.domain.usecase.db.workintopic.UpdateWorkUseCase
import com.example.reminds.common.BaseViewModel
import com.example.reminds.ui.fragment.focus.home.STATE
import com.example.reminds.utils.TimestampUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusActivityViewModel @Inject constructor(private val updateWorkUseCase: UpdateWorkUseCase) : BaseViewModel() {
    val itemWorkSelected: MutableLiveData<WorkDataEntity?> = MutableLiveData()

    fun doneAllInWork() = viewModelScope.launch(Dispatchers.IO + handler) {
        itemWorkSelected.value?.let {
            it.doneAll = true
            updateWorkUseCase.invoke(UpdateWorkUseCase.Param(it))
            itemWorkSelected.postValue(null)
        }
    }

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
