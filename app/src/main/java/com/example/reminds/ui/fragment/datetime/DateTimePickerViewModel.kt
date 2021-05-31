package com.example.reminds.ui.fragment.datetime

import androidx.lifecycle.MutableLiveData
import com.example.reminds.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DateTimePickerViewModel @Inject constructor() : BaseViewModel() {
    val minimum = MutableLiveData<Calendar>()
    val maximum = MutableLiveData<Calendar>()
    val calendar = MutableLiveData<Calendar>()

    fun onDateChanged(year: Int, month: Int, day: Int) {
        val newDate = calendar.value
        newDate?.set(year, month, day)
        calendar.value = newDate
    }

    fun onTimeChanged(hourOfDay: Int, minute: Int) {
        val newTime = calendar.value
        newTime?.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newTime?.set(Calendar.MINUTE, minute)
        calendar.value = newTime
    }


    fun setUpTime(time: Long, min: Long, max: Long) {
        calendar.value = Calendar.getInstance().apply { timeInMillis = time }
        if (min > 0L) {
            minimum.value = Calendar.getInstance().apply { timeInMillis = min }
        }
        if (max > 0L) {
            maximum.value = Calendar.getInstance().apply { timeInMillis = max }
        }

    }
}