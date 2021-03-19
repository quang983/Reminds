package com.example.reminds.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.reminds.AppComponent
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.*
import java.io.Closeable
import java.io.IOException
import java.net.UnknownHostException
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

open class BaseFirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        private const val JOB_KEY = "androidx.lifecycle.ViewModelCoroutineScope.JOB_KEY"

    }

    private var mCleared = false

    private val mBagOfTags = HashMap<String, Any>()

    private val listBroadcastReceiver = CopyOnWriteArrayList<BroadcastReceiver>()

    val tags: MutableMap<String, Any> = HashMap()

    val handler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        if (throwable !is UnknownHostException) {
            ExceptionBus.instance.bindException(throwable)
        }
    }

    protected open fun registerReceiver(vararg action: String, receiver: (Intent) -> Unit) {
        registerReceiver(receiver, *action)
    }

    protected open fun registerReceiver(receiver: (Intent) -> Unit, vararg action: String) {
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { receiver.invoke(it) }
            }
        }, *action)
    }

    protected open fun registerReceiver(receiver: BroadcastReceiver, vararg action: String) {
        listBroadcastReceiver.add(receiver)
        for (s in action) {
            LocalBroadcastManager.getInstance(AppComponent.shared).registerReceiver(receiver, IntentFilter(s))
        }
    }

    fun sendBroadcast(action: String, block: Intent.() -> Unit) {
        val intent = Intent(action).apply { block(this) }
        LocalBroadcastManager.getInstance(AppComponent.shared).sendBroadcast(intent)
    }

    override fun onDestroy() {
        mCleared = true

        for (broadcastReceiver in listBroadcastReceiver) {
            LocalBroadcastManager.getInstance(AppComponent.shared).unregisterReceiver(broadcastReceiver)
        }

        synchronized(mBagOfTags) {
            for (value in mBagOfTags.values) {
                closeWithRuntimeException(value)
            }
        }

        super.onDestroy()
    }

    protected fun getDispatcher(tag: String): CoroutineDispatcher {
        return getOrCreateAny("DISPATCHER-$tag") {
            Executors.newFixedThreadPool(1).asCoroutineDispatcher()
        }
    }

    inline fun <reified T : Any> getOrCreateAny(tag: String, block: () -> T): T {
        var any: T
        synchronized(tags) {
            any = (tags[tag] as? T) ?: block.invoke()
            tags.put(tag, any)
        }
        return any
    }

    inline fun <reified T : Any> putAny(tag: String, t: T) {
        synchronized(tags) {
            tags.put(tag, t)
        }
    }

    inline fun <reified T> getAnyIf(block: (String, Any) -> Boolean): List<T> {
        val list: MutableList<T> = ArrayList()
        synchronized(tags) {
            tags.forEach {
                if (block.invoke(it.key, it.value)) list.add(it.value as T)
            }
        }
        return list
    }

    inline fun <reified T : Any> getAny(tag: String): T? {
        var any: T?
        synchronized(tags) {
            any = tags[tag] as? T
        }
        return any
    }

    protected fun removeAny(tag: String) {
        synchronized(tags) {
            tags.remove(tag)
        }
    }

    open fun getCoroutineScope(key: String, context: CoroutineContext = Dispatchers.IO): CoroutineScope {
        var scope: CoroutineScope? = this.getTag(key)
        if (scope == null) {
            scope = setTagIfAbsent(key, CloseableCoroutineScope(SupervisorJob() + context))
        }
        return scope
    }

    @SuppressWarnings("unchecked")
    open fun <T : Any> setTagIfAbsent(key: String, newValue: T): T {
        var previous: T?
        synchronized(mBagOfTags) {
            previous = mBagOfTags[key] as? T
            if (previous == null) {
                mBagOfTags[key] = newValue
            }
        }
        val result: T = if (previous == null) newValue else previous!!
        if (mCleared) {
            closeWithRuntimeException(result)
        }
        return result
    }

    @SuppressWarnings("TypeParameterUnusedInFormals", "unchecked")
    open fun <T : Any> getTag(key: String?): T? {
        synchronized(mBagOfTags) {
            return mBagOfTags[key] as? T
        }
    }

    private fun closeWithRuntimeException(obj: Any?) {
        if (obj is Closeable) {
            try {
                obj.close()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}