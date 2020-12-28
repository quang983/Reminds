package com.example.reminds.model

import SingleLiveEvent
import androidx.lifecycle.LiveData
import com.example.reminds.utils.postValue

class ToastBus private constructor() {

    private object HOLDER {
        val INSTANCE = ToastBus()
    }

    companion object {
        val instance: ToastBus by lazy { HOLDER.INSTANCE }
    }

    val toast: LiveData<String> = SingleLiveEvent<String>()

    fun bindToast(e: String) = toast.postValue(e)
}