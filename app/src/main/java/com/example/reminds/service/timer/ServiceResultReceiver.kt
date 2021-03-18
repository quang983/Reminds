package com.example.reminds.service.timer

import android.os.Bundle
import android.os.Handler


class ServiceResultReceiver {
    private var mReceiver: Receiver? = null

    /**
     * Create a new ResultReceive to receive results.  Your
     * [.onReceiveResult] method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler the handler object
     */
    fun ServiceResultReceiver(handler: Handler?) {
    }

    fun setReceiver(receiver: Receiver?) {
        mReceiver = receiver
    }


    protected fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        if (mReceiver != null) {
            mReceiver!!.onReceiveResult(resultCode, resultData)
        }
    }

    interface Receiver {
        fun onReceiveResult(resultCode: Int, resultData: Bundle?)
    }
}