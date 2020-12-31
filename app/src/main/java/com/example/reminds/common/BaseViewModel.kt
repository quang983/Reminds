package com.example.reminds.common

import SingleLiveEvent
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.example.reminds.utils.DebounceLiveData
import com.example.reminds.utils.exception.ExceptionHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import com.example.reminds.model.ToastBus
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference

abstract class BaseViewModel : ViewModel {

    companion object {
        private const val TAG = "BaseViewModel"
    }

    constructor() : super() {
    }

    @Deprecated("")
    constructor(exceptionHandler: ExceptionHandler) : super() {
    }

    protected val toast: LiveData<String> = SingleLiveEvent<String>()

    val handler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        ExceptionBus.instance.bindException(throwable)
    }

    init {
        ToastBus.instance.toast.addSource(toast) {
            ToastBus.instance.bindToast(it)
        }
    }

    override fun onCleared() {
        (ToastBus.instance.toast as? MediatorLiveData)?.removeSource(toast)
        super.onCleared()
    }

    protected fun getScopeDefault(): CoroutineScope {
        return viewModelScope
    }

    protected fun getContextDefault(): CoroutineContext {
        return handler + Dispatchers.IO
    }

    @OptIn(ExperimentalTypeInference::class)
    open fun <Y> liveDataEmit(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        @BuilderInference block: suspend () -> Y
    ): LiveData<Y> = androidx.lifecycle.liveData(handler + dispatcher) {
        emit(block())
    }

    @OptIn(ExperimentalTypeInference::class)
    fun <Y> liveData(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        @BuilderInference block: suspend LiveDataScope<Y>.() -> Unit
    ): LiveData<Y> = androidx.lifecycle.liveData(handler + dispatcher) {
        block()
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

    @MainThread
    fun <T> LiveData<T>.checkDiffAndPostValue(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        block: suspend CoroutineScope.(T?) -> T?
    ) {
        val currentValue = value
        viewModelScope.launch(handler + dispatcher) {
            val newValue = block.invoke(this, currentValue)
            if (currentValue == null && newValue == null) return@launch
            if (currentValue?.equals(newValue) != true) (this@checkDiffAndPostValue as? MutableLiveData<T>)?.postValue(newValue)
        }
    }

    @AnyThread
    fun <T> LiveData<T>?.postValue(t: T) {
        when (this) {
            is SingleLiveEvent<T> -> this.postValue(t)
            is MediatorLiveData<T> -> this.postValue(t)
            is MutableLiveData<T> -> this.postValue(t)
            is DebounceLiveData<T> -> this.postValue(t)
        }
    }

    @MainThread
    fun <T> LiveData<T>.combineSources(sources: List<LiveData<*>>, onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }): LiveData<T> {
        return combineSources(*sources.toTypedArray()) {
            onChanged.invoke(this)
        }
    }

    @MainThread
    fun <T> LiveData<T>.combineSources(vararg sources: LiveData<*>, onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }): LiveData<T> {
        return addSources(*sources) {
            for (source in sources) {
                if (source.value == null) return@addSources
            }
            onChanged(this)
        }
    }

    @MainThread
    fun <T> LiveData<T>.addSources(sources: List<LiveData<*>>, onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }): LiveData<T> {
        return addSources(*sources.toTypedArray()) {
            onChanged.invoke(this)
        }
    }

    @MainThread
    fun <T> LiveData<T>.addSources(vararg sources: LiveData<*>, onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }): LiveData<T> {
        sources.forEach { item ->
            addSource(item) {
                onChanged(this)
            }
        }
        return this
    }

    @MainThread
    fun <T, S> LiveData<T>.addSource(source: LiveData<S>, onChanged: MediatorLiveData<T>.(S) -> Unit = { postValue(null) }): LiveData<T> {
        (this as? MediatorLiveData<T>)?.let {
            addSource(source) {
                onChanged(this, it)
            }
        } ?: throw RuntimeException("LiveData isn't MediatorLiveData")
        return this
    }

    @MainThread
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> LiveData<T>.launch(context: CoroutineContext = handler + Dispatchers.IO, onChanged: suspend (T) -> Unit = {}) = callbackFlow<T> {
        val observer = Observer<T> { offer(it) }

        observeForever(observer)

        awaitClose {
            removeObserver(observer)
        }
    }.flowOn(Dispatchers.Main).map {
        onChanged.invoke(it)
    }.flowOn(context).launchIn(viewModelScope)

}

@Deprecated("")
fun <X, Y> LiveData<X>.switchMapLiveDataEmit(
    coroutineScope: CoroutineScope,
    context: CoroutineContext = Dispatchers.IO,
    block: suspend CoroutineScope.(X) -> Y
) = switchMap {
    androidx.lifecycle.liveData(context) {
        emit(block(coroutineScope, it))
    }
}

@Deprecated("")
fun <X, Y> LiveData<X>.switchMapLiveData(
    coroutineScope: CoroutineScope,
    context: CoroutineContext = Dispatchers.IO,
    block: suspend CoroutineScope.(LiveDataScope<Y>, X) -> Unit
) = switchMap {
    androidx.lifecycle.liveData<Y>(context) {
        block(coroutineScope, this@liveData, it)
    }
}

@Deprecated("")
fun <X, Y> LiveData<X>.switchMapLiveDataEmit(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend (X) -> Y
) = switchMapLiveData<X, Y>(context) {
    emit(block(it))
}

@Deprecated("")
fun <X, Y> LiveData<X>.switchMapLiveData(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend LiveDataScope<Y>.(X) -> Unit
) = switchMap {
    liveData<Y>(context) {
        block(it)
    }
}

@MainThread
@Deprecated("Dùng hàm trong extention com.example.reminds.utils.getOrDefault")
fun <X> LiveData<X>.getOrDefault(default: X): X {
    return value ?: default
}

@MainThread
@Deprecated(" Dùng hàm trong extention com.example.reminds.utils.getOrNull")
fun <X> LiveData<X>.getOrNull(): X? {
    return value
}

@Deprecated("")
fun <Y> liveDataEmit(
    context: CoroutineContext = Dispatchers.IO,
    block: suspend () -> Y
) = liveData(context) {
    emit(block())
}

@Deprecated("")
@OptIn(ExperimentalTypeInference::class)
fun <Y> liveData(
    context: CoroutineContext = Dispatchers.IO,
    @BuilderInference block: suspend LiveDataScope<Y>.() -> Unit
) = androidx.lifecycle.liveData<Y>(context) {
    block()
}


