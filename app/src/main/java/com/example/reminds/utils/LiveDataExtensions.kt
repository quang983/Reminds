package com.example.reminds.utils

import SingleLiveEvent
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.example.reminds.common.RetrieveDataState
import kotlinx.coroutines.*

@AnyThread
fun <T> LiveData<T>?.postValue(t: T) {
    when (this) {
        is SingleLiveEvent<T> -> this.postValue(t)
        is MediatorLiveData<T> -> this.postValue(t)
        is MutableLiveData<T> -> this.postValue(t)
        is DebounceLiveData<T> -> this.postValue(t)
    }
}

fun <Y, X> LiveData<Map<Y, X>>.getOrEmpty(): Map<Y, X> {
    return getOrDefault(emptyMap())
}

fun <X> LiveData<List<X>>.getOrEmpty(): List<X> {
    return getOrDefault(emptyList())
}

fun <X> LiveData<X>.getOrDefault(default: X): X {
    return value ?: default
}

fun <X> LiveData<X>.getOrNull(): X? {
    return value
}

@MainThread
fun <T> LiveData<T>.combineSources(
    sources: List<LiveData<*>>,
    onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }
): LiveData<T> {
    return combineSources(*sources.toTypedArray()) {
        onChanged.invoke(this)
    }
}

@MainThread
fun <T> LiveData<T>.combineSources(
    vararg sources: LiveData<*>,
    onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }
): LiveData<T> {
    return addSources(*sources) {
        for (source in sources) {
            if (source.value == null) return@addSources
        }
        onChanged(this)
    }
}

@MainThread
fun <T> LiveData<T>.addSources(
    sources: List<LiveData<*>>,
    onChanged: MediatorLiveData<T>.() -> Unit = {}
): LiveData<T> {
    return addSources(*sources.toTypedArray()) {
        onChanged.invoke(this)
    }
}

@MainThread
fun <T> LiveData<T>.addSources(
    vararg sources: LiveData<*>,
    onChanged: MediatorLiveData<T>.() -> Unit = {}
): LiveData<T> {
    sources.forEach { item ->
        addSource(item) {
            onChanged(this)
        }
    }
    return this
}

@MainThread
fun <T, S> LiveData<T>.addSource(
    source: LiveData<S>,
    onChanged: MediatorLiveData<T>.(S) -> Unit = {}
): LiveData<T> {
    (this as? MediatorLiveData<T>)?.addSource(source) {
        onChanged(this, it)
    }
    return this
}

@MainThread
fun <T, S> LiveData<T>.removeSource(source: LiveData<S>): LiveData<T> {
    (this as? MediatorLiveData<T>)?.removeSource(source)
    return this
}

@MainThread
fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, func: (T) -> (Unit)) {
    removeObservers(owner)
    observe(owner, Observer<T> { t -> func(t) })
}

fun <T> LiveData<RetrieveDataState<T>>.getSuccessValue(): T? {
    return this.value?.let {
        when (it) {
            is RetrieveDataState.Success -> it.data
            else -> null
        }
    }
}

fun <T> LiveData<RetrieveDataState<T>>.isFinished(): Boolean {
    return when (this.value) {
        is RetrieveDataState.Success -> true
        is RetrieveDataState.Failure -> true
        else -> false
    }
}

@MainThread
fun <X> LiveData<X>.breakFirst(): LiveData<X> {
    val outputLiveData = MediatorLiveData<X>()
    outputLiveData.addSource(this, object : Observer<X> {
        var mFirstTime = true
        override fun onChanged(currentValue: X) {
            if (!mFirstTime && currentValue != null) {
                outputLiveData.value = currentValue ?: error("")
            }
            mFirstTime = false
        }
    })
    return outputLiveData
}

class DebounceLiveData<T>(private val scope: CoroutineScope, private val delay: Long = 300L) : MediatorLiveData<T>() {

    private var job: Job? = null

    override fun postValue(value: T) {
        postValue(value, null)
    }

    fun postValue(value: T, delay: Long? = null) {
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            delay(delay ?: this@DebounceLiveData.delay)
            super.postValue(value)
        }
    }

    override fun onInactive() {
        super.onInactive()
        job?.cancel()
    }

}
