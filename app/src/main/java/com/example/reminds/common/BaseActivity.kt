package com.example.reminds.common

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    protected lateinit var mBinding: VB

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        mBinding = getViewBinding()
        setContentView(mBinding.root)
    }

    abstract fun getViewBinding(): VB

/*    *//** Flag indicating whether we have called bind on the service.  *//*
    private var bound: Boolean = false
    private var mService: Messenger? = null
    private var mMessenger: Messenger? = null
    lateinit var intentService: Intent

    *//**
     * Class for interacting with the main interface of the service.
     *//*

    private fun onStartService() {
        intentService = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(this, NotificationService::class.java).also { intent ->
                startService(intent)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }

        } else {
            Intent(this, NotificationService::class.java).also { intent ->
                startService(intent)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            bound = false
        }
    }

    private fun sendActionInsertAlert(data: AlarmNotificationEntity) {
        if (!bound) return
        val msg: Message = Message.obtain(null, INSERT_OBJECT_TIMER_DATA, data)
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }*/
}