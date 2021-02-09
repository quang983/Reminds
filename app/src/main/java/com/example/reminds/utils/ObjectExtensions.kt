package com.example.reminds.utils

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.res.Resources
import android.util.TypedValue
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun <T> io.reactivex.Observable<T>.execute() {
    subscribe(object : Observer<T> {
        override fun onSubscribe(d: Disposable) {
        }

        override fun onNext(t: T) {
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }

        override fun onComplete() {
        }
    })
}

/*

class ObjectMapperProvider {

    private object HOLDER {
        val INSTANCE = ObjectMapperProvider()
    }

    companion object {
        val instance: ObjectMapperProvider by lazy { HOLDER.INSTANCE }
    }

    val objectMapper: ObjectMapper = ObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false)
        objectMapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP))
        objectMapper.setVisibility(
            objectMapper.serializationConfig.defaultVisibilityChecker
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
                .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        )
    }

}

inline fun <T, R> T.mapObject(transform: (T) -> R): R {
    return transform.invoke(this)
}


fun Any.toJson(): String {
    return ObjectMapperProvider.instance.objectMapper.writeValueAsString(this)
}

fun Any.toJson(vararg classes: Class<*>): String {
    val mapper = ObjectMapper()
    mapper.setAnnotationIntrospector(
        DisablingJsonIgnoreIntrospector(
            *classes
        )
    )
    return mapper.writeValueAsString(this)
}

fun <T> String.toObject(clazz: Class<T>): T {
    return ObjectMapperProvider.instance.objectMapper.readValue(this, clazz)
}

inline fun <reified T> String.toObject(): T {
    return ObjectMapperProvider.instance.objectMapper.readValue(this, T::class.java)
}

inline fun <reified T> String.toListObject(): List<T> {
    val mapper = ObjectMapper()
    val listType = mapper.typeFactory.constructCollectionType(List::class.java, T::class.java)
    return mapper.readValue(this, listType)
}

fun String.normalize(): String {
    return Utils.normalize(this)
}
*/

fun ArrayList<ValuesHolder>.animation(
    duration: Long,
    onStart: () -> Unit,
    onUpdate: (HashMap<String, Any>, ValueAnimator) -> Unit,
    onEnd: (ValueAnimator) -> Unit
): ValueAnimator {
    return animation(duration, 0, onStart, onUpdate, onEnd)
}

fun ArrayList<ValuesHolder>.animation(
    duration: Long,
    startDelay: Long,
    onStart: () -> Unit,
    onUpdate: (HashMap<String, Any>, ValueAnimator) -> Unit,
    onEnd: (ValueAnimator) -> Unit
): ValueAnimator {
    val array = arrayOfNulls<ValuesHolder>(size)
    toArray(array)
    return array.animation(duration, startDelay, onStart, onUpdate, onEnd)
}

fun Array<ValuesHolder?>.animation(
    duration: Long,
    startDelay: Long,
    onStart: () -> Unit,
    onUpdate: (HashMap<String, Any>, ValueAnimator) -> Unit,
    onEnd: (ValueAnimator) -> Unit
): ValueAnimator {

    val keys = ArrayList<String>()
    val tos = ArrayList<Any>()
    for (valuesHolder in this) {
        keys.add(valuesHolder!!.key)
        tos.add(valuesHolder.to)
    }

    val animator = ValueAnimator.ofPropertyValuesHolder(*this.to())

    animator.addUpdateListener {
        val keyData = HashMap<String, Any>()
        val currentData = ArrayList<Any>()


        for (key in keys) {
            val data = it.getAnimatedValue(key)
            keyData[key] = data
            currentData.add(data)
        }

        onUpdate(keyData, it)

        var equals = true
        for (i in tos.indices) {
            if (tos[i] != currentData[i]) equals = false
        }
        if (equals) {
            it.cancel()
        }
    }

    animator.withEndAction(Runnable {
        onStart()
    }, Runnable {
        onEnd(animator)
    })

    animator.duration = duration
    animator.startDelay = startDelay
    animator.start()

    return animator
}

fun Array<ValuesHolder?>.to(): Array<PropertyValuesHolder> {
    return Array(size) { i -> this[i]!!.to() }
}

class ValuesHolder(val key: String, val from: Any, val to: Any) {

    fun to(): PropertyValuesHolder {
        return if (from is Int && to is Int) {
            PropertyValuesHolder.ofInt(key, from, to)
        } else {
            PropertyValuesHolder.ofFloat(key, (from as Float), (to as Float))
        }
    }
}

fun Animator.withEndAction(start: Runnable, end: Runnable) {
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            start.run()
        }

        override fun onAnimationEnd(animation: Animator?) {
            animation?.removeAllListeners()
            end.run()
        }

    })
}

fun <T> List<T>.findFirstValidate(predicate: T.() -> Boolean, validate: T.() -> T): List<T> {
    val list = ArrayList(this)
    list.forEachIndexed { index, item ->

        if (predicate(item)) {
            list[index] = validate(item)
            return list
        }

    }

    return list
}

fun <T> List<T>.findValidate(predicate: T.() -> Boolean, validate: T.() -> T): List<T> {
    val list = ArrayList(this)

    list.forEachIndexed { index, item ->

        if (predicate(item)) {
            list[index] = validate(item)
        }

    }

    return list
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

fun Int.toPx(): Int {
    return this.toFloat().toPx()
}

fun Float.toPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()
}

fun Timer.schedule(delay: Long, period: Long, run: () -> Unit) {
    schedule(delay, period, Runnable { run.invoke() })
}

fun Timer.schedule(delay: Long, period: Long, run: Runnable) {
    schedule(object : TimerTask() {
        override fun run() {
            run.run()
        }
    }, delay, period)
}


/*inline fun <reified P : Interact.Param, E> Interact<P, Flow<E>>.asLiveData(context: CoroutineContext = EmptyCoroutineContext, p: P? = null) = liveData(context) {
    execute(p).collect {
        emit(it)
    }
}

inline fun <reified P : Interact.Param, E> Interact<P, Flow<E>>.asLiveData(context: CoroutineContext = EmptyCoroutineContext, listener: LiveData<*>, p: P? = null) = listener.switchMap {
    liveData(context) {
        execute(p).collect {
            emit(it)
        }
    }*/
