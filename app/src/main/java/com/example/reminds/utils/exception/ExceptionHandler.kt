package com.example.reminds.utils.exception

import android.util.Log
import com.example.reminds.BuildConfig

class ExceptionHandler(private val exceptionFactories: ArrayList<ExceptionFactory>) {

    companion object {
        private const val TAG = "ExceptionHandler"
    }

    fun process(cause: Throwable): KvException {
        if (cause !is BreakException) Log.d(TAG, "process: ", cause)
        var error: KvException? = null
        try {
            for (filter in exceptionFactories) {
                error = filter.buildError(cause)
                if (error != null) break
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                error = ToastException(Int.MIN_VALUE, e.message ?: "Chưa xác định", e)
            }
        }
        if (error == null) {
            error = KvException()
        }
        return error
    }

}

interface ExceptionFactory {

    fun buildError(cause: Throwable): KvException?
}

open class KvException(
    code: Int = 0,
    override val message: String = "",
    override val cause: Throwable? = null
) : Throwable(message, cause)

class BreakException : KvException {

    constructor(message: String = "") : super(0, message)

    constructor(code: Int = 0, message: String = "") : super(code, message)
}

data class ToastException(
    val code: Int = 0,
    override val message: String = "",
    override val cause: Throwable? = null
) : KvException(code, message, cause)