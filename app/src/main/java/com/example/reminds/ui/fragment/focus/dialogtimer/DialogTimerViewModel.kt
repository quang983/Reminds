package com.example.reminds.ui.fragment.focus.dialogtimer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import com.example.reminds.common.BaseViewModel

class DialogTimerViewModel @ViewModelInject constructor() : BaseViewModel() {
    private val _listMinute = ArrayList<Int>()

    val listMinuteItemView: MediatorLiveData<List<Int>> = MediatorLiveData()

    var minuteSelectedItem: Long = 0

    init {
        for (i in 3..122) {
            _listMinute.add(i)
        }
        listMinuteItemView.postValue(_listMinute)
    }

}