package com.example.reminds.ui.fragment.focus.dialogtimer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reminds.common.BaseViewModel

class DialogTimerViewModel @ViewModelInject constructor() : BaseViewModel() {
    private val _listMinute = ArrayList<Int>()

    val listMinuteItemView: LiveData<List<Int>> = MutableLiveData()

    var minuteSelectedItem: Long = 0

    init {
        for (i in 3..122) {
            _listMinute.add(i)
        }
        listMinuteItemView.postValue(_listMinute)
    }
}