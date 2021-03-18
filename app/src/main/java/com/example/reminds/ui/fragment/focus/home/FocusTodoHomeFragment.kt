package com.example.reminds.ui.fragment.focus.home

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentHomeFocusBinding
import com.example.reminds.service.timer.NotificationTimer
import com.example.reminds.service.timer.TimerService
import com.example.reminds.service.timer.TimerState
import com.example.reminds.ui.activity.focus.FocusTodoActivity

class FocusTodoHomeFragment : BaseFragment<FragmentHomeFocusBinding>() {
    lateinit var notiTimer: NotificationTimer.Builder

    override fun getViewBinding(): FragmentHomeFocusBinding {
        return FragmentHomeFocusBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        val pendingIntent = Intent(requireContext(), FocusTodoActivity::class.java).let {
            PendingIntent.getActivity(requireContext(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        mBinding.buildBtn.setOnClickListener {
            notiTimer = NotificationTimer.Builder(requireContext())
                .setSmallIcon(R.drawable.ic_creativity)
                .setPlayButtonIcon(R.drawable.ic_creativity)
                .setPauseButtonIcon(R.drawable.ic_creativity)
                .setStopButtonIcon(R.drawable.ic_creativity)
                .setControlMode(true)
                .setColor(R.color.blue_700)
                .setShowWhen(false)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOnTickListener { mBinding.timeUntilFinishText.text = it.toString() }
                .setOnFinishListener { Toast.makeText(requireContext(), "timer finished", Toast.LENGTH_SHORT).show() }
                .setContentTitle("Timer :)")
        }
        mBinding.playBtn.setOnClickListener {
            if (TimerService.state != TimerState.RUNNING) {
                Intent(context, TimerService::class.java).apply {
                    action = "PLAY"
                    putExtra("setTime", mBinding.timeEditText.text.toString().toLong())
                    putExtra("forReplay", TimerService.state == TimerState.PAUSED)
                }
            }
        }

        mBinding.pauseBtn.setOnClickListener {
            notiTimer.pause()
        }

        mBinding.stopBtn.setOnClickListener {
            notiTimer.stop()
            mBinding.timeUntilFinishText.text = null
        }

        mBinding.terminateBtn.setOnClickListener {
            notiTimer.terminate()
        }
    }
}