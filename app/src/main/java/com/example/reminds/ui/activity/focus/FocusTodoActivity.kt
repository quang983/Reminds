package com.example.reminds.ui.activity.focus

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Messenger
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.reminds.R
import com.example.reminds.databinding.ActivityFocusTodoBinding
import com.example.reminds.service.timer.TimerService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FocusTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFocusTodoBinding
    private lateinit var navController: NavController
    lateinit var intentService: Intent

    private var bound: Boolean = false
    private var mService: Messenger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusTodoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavController()
        onStartService()
    }

    private fun setupNavController() {
        navController = findNavController(R.id.nav_host_focus_fragment)
    }

    private fun onStartService() {
        super.onStart()
        intentService = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(this, TimerService::class.java).also { intent ->
                startForegroundService(intent)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }

        } else {
            Intent(this, TimerService::class.java).also { intent ->
                startService(intent)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }

        }
    }


    /**
     * Class for interacting with the main interface of the service.
     */
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

}