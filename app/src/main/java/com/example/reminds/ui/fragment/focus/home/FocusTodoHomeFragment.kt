package com.example.reminds.ui.fragment.focus.home

import android.animation.ValueAnimator
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentHomeFocusBinding
import com.example.reminds.service.timer.NotificationTimer
import com.example.reminds.ui.activity.focus.FocusTodoActivity
import com.example.reminds.ui.fragment.focus.dialogtimer.DialogTimerFragment
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.getOrDefault
import com.example.reminds.utils.navigate
import com.example.reminds.utils.setOnClickListenerBlock
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FocusTodoHomeFragment : BaseFragment<FragmentHomeFocusBinding>() {
    lateinit var notiTimer: NotificationTimer.Builder

    private val viewModel: FocusTodoHomeViewModel by viewModels()

    lateinit var animator: ValueAnimator

    companion object {
        const val RESULTS_MINUTES_PICKER = "RESULTS_MINUTES_PICKER"
    }

    override fun getViewBinding(): FragmentHomeFocusBinding {
        return FragmentHomeFocusBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResult()
    }

    private fun setFragmentResult() {
        setFragmentResultListener(RESULTS_MINUTES_PICKER) { _, bundle ->
            viewModel.mTimeLeftInMillis.postValue(bundle.getLong(DialogTimerFragment.EXTRAS_MINUTES_DATA))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupObserver()
        setupListener()
    }

    private fun setupLayout() {
        simulateProgress()

        mBinding.tvTime.text = TimestampUtils.convertMiliTimeToTimeHourStr(viewModel.mTimeLeftInMillis.getOrDefault(10000))

        val pendingIntent = Intent(requireContext(), FocusTodoActivity::class.java).let {
            PendingIntent.getActivity(requireContext(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        notiTimer = NotificationTimer.Builder(requireContext())
            .setSmallIcon(R.drawable.playstore_icon)
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
            if (viewModel.mTimerRunning == STATE.INDIE || viewModel.mTimerRunning == STATE.PAUSE) {
                startTimer()
            } else {
                pauseTimer()
            }
        }
    }

    private fun startTimer() {
        mBinding.btnStart.text = "Pause"
        animator.duration = viewModel.mTimeLeftInMillis.value ?: 100000
        viewModel.startTimer()
        notiTimer.play(viewModel.mTimeLeftInMillis.value ?: 100000)
        if (animator.isStarted) {
            animator.resume()
        } else {
            animator.start()
        }
    }

    private fun pauseTimer() {
        mBinding.btnStart.text = "Start"
        viewModel.pauseTimer()
        notiTimer.pause()
        animator.pause()
    }

    private fun setupObserver() {
        viewModel.timeShowLiveData.observe(viewLifecycleOwner, {
            mBinding.tvTime.text = it.toString()
        })
    }

    private fun setupListener() {
        mBinding.tvTime.setOnClickListener {
            navigate(FocusTodoHomeFragmentDirections.actionFocusTodoFragmentToPickTimerFocusFragment())
        }
        mBinding.btnAddTask.setOnClickListenerBlock {

        }
    }


    private fun simulateProgress() {
        animator = ValueAnimator.ofInt(0, 100)
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            mBinding.circleCustom.progress = progress
        }
        animator.repeatCount = 0
    }
}