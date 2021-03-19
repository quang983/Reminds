package com.example.reminds.ui.fragment.focus.home

import android.animation.ValueAnimator
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
import com.example.reminds.ui.activity.focus.FocusTodoActivity
import kotlinx.android.synthetic.main.fragment_home_focus.*


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
            .setOnTickListener { /*mBinding.timeUntilFinishText.text = it.toString()*/ }
            .setOnFinishListener { Toast.makeText(requireContext(), "timer finished", Toast.LENGTH_SHORT).show() }
            .setContentTitle("Timer :)")

        mBinding.btnStart.setOnClickListener {
            notiTimer.play(time_editText.text.toString().toLong())
            simulateProgress(time_editText.text.toString().toLong())
        }

        /*       mBinding.pauseBtn.setOnClickListener {
                   notiTimer.pause()
               }

               mBinding.stopBtn.setOnClickListener {
                   notiTimer.stop()
                   mBinding.timeUntilFinishText.text = null
               }

               mBinding.terminateBtn.setOnClickListener {
                   notiTimer.terminate()
               }*/
    }


    private fun simulateProgress(duration : Long) {
        val animator = ValueAnimator.ofInt(0, 100)
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            mBinding.circleCustom.progress = progress
        }
        animator.repeatCount =0
        animator.duration = duration
        animator.start()
    }
}