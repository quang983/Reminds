package com.example.reminds.ui.fragment.focus.dialogtimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reminds.common.BaseViewModel

class DialogTimerViewModel : BaseViewModel() {
    private val _listMinute = ArrayList<Int>()

    val listMinuteItemView: LiveData<List<Int>> = MutableLiveData()

    init {
        for (i in 1..120) {
            _listMinute.add(i)
        }
        listMinuteItemView.postValue(_listMinute)
    }
}