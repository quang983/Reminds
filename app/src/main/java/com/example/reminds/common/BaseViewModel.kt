package com.example.reminds.common

import SingleLiveEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference

abstract class BaseViewModel : ViewModel() {
    val exception: LiveData<Throwable> = SingleLiveEvent<Throwable>()

    val handler = CoroutineExceptionHandler{ _: CoroutineContext, throwable: Throwable->
//        exception.post = throwable
    }

    @OptIn(ExperimentalTypeInference::class)
    fun <Y> liveData(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        @BuilderInference block: suspend () -> Y
    ) : LiveData<Y> = androidx.lifecycle.liveData(handler + dispatcher){
        emit(block())
    }

    @OptIn(ExperimentalTypeInference::class)
    open fun <Y> liveDataEmit(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        @BuilderInference block: suspend () -> Y
    ): LiveData<Y> = androidx.lifecycle.liveData(handler + dispatcher) {
        emit(block())
    }


    open fun <X, Y> LiveData<X>.switchMapLiveDataEmit(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend (X) -> Y
    ): LiveData<Y> = switchMap {
        androidx.lifecycle.liveData(handler + dispatcher) {
            emit(block(it))
        }
    }

    fun <X, Y> LiveData<X>.switchMapLiveData(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend LiveDataScope<Y>.(X) -> Unit
    ): LiveData<Y> = switchMap {
        androidx.lifecycle.liveData<Y>(handler + dispatcher) {
            block(this@liveData, it)
        }
    }

}