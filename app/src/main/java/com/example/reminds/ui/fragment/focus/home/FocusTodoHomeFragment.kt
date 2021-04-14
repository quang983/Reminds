package com.example.reminds.ui.fragment.focus.home

import android.animation.ValueAnimator
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentHomeFocusBinding
import com.example.reminds.service.timer.NotificationTimer
import com.example.reminds.ui.activity.focus.FocusTodoActivity
import com.example.reminds.ui.fragment.focus.dialogtimer.DialogTimerFragment
import com.example.reminds.ui.sharedviewmodel.FocusActivityViewModel
import com.example.reminds.utils.gone
import com.example.reminds.utils.navigate
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast

@AndroidEntryPoint
class FocusTodoHomeFragment : BaseFragment<FragmentHomeFocusBinding>() {
    lateinit var notiTimer: NotificationTimer.Builder

    private val viewModel: FocusTodoHomeViewModel by viewModels()

    val viewModelShared: FocusActivityViewModel by activityViewModels()

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
            viewModel.setTimerData(bundle.getLong(DialogTimerFragment.EXTRAS_MINUTES_DATA))
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
        val pendingIntent = Intent(requireContext(), FocusTodoActivity::class.java).let {
            PendingIntent.getActivity(requireContext(), 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        notiTimer = NotificationTimer.Builder(requireActivity())
            .setSmallIcon(R.drawable.playstore_icon)
            .setControlMode(true)
            .setColor(R.color.blue_700)
            .setShowWhen(false)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setOnTickListener { }
            .setOnFinishListener { }
            .setContentTitle("Timer :)")

        mBinding.btnStart.setOnClickListener {
            if (viewModel.timerRunningStateLiveData.value == STATE.INDIE || viewModel.timerRunningStateLiveData.value == STATE.PAUSE) {
                startTimer()
            } else {
                pauseTimer()
            }
        }
    }

    private fun startTimer() {
        animator.duration = viewModel.mTimeLeftInMillis
        viewModel.startTimer()
        notiTimer.play(viewModel.mTimeLeftInMillis)
        if (animator.isStarted) {
            animator.resume()
        } else {
            animator.start()
        }
    }

    private fun pauseTimer() {
        viewModel.pauseTimer()
        notiTimer.pause()
        animator.pause()
    }

    private fun setupObserver() {
        viewModel.timeShowLiveData.observe(viewLifecycleOwner, {
            mBinding.tvTime.text = it.toString()
        })

        viewModelShared.itemWorkSelected.observe(viewLifecycleOwner, {
            it?.let {
                mBinding.tvWorkName.text = it.name
            }
        })

        viewModel.timerRunningStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                STATE.INDIE -> {
                    notiTimer.terminate()
                    animator.cancel()
                    mBinding.btnReset.gone()
                    mBinding.btnStart.text = "Start"
                }
                STATE.RESUME -> {
                    mBinding.btnReset.visible()
                    mBinding.btnStart.text = "Pause"
                }
                STATE.PAUSE -> {
                    mBinding.btnReset.visible()
                    mBinding.btnStart.text = "Continue"
                }
                STATE.FINISH -> {
                    notiTimer.terminate()
                    animator.cancel()
                    mBinding.btnReset.gone()
                    mBinding.btnStart.text = "Start"
                    //show man hinh chuc mung
                    viewModel.resetState()
                    viewModelShared.doneAllInWork()
                    navigate(FocusTodoHomeFragmentDirections.actionFocusTodoFragmentToSuccessFocusFragment())
                }
                else -> {
                    mBinding.tvTime.isClickable = false
                    mBinding.btnReset.gone()
                }
            }
        })
    }

    private fun setupListener() {
        mBinding.tvTime.setOnClickListener {
            if (viewModel.timerRunningStateLiveData.value == STATE.INDIE) {
                navigate(FocusTodoHomeFragmentDirections.actionFocusTodoFragmentToPickTimerFocusFragment())
            } else {
                MotionToast.createToast(
                    requireActivity(),
                    getString(R.string.notify_title),
                    resources.getString(R.string.error_messenger_change_time),
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(requireActivity(), R.font.roboto_medium)
                )
            }
        }

        mBinding.btnAddTask.setOnClickListenerBlock {
            navigate(FocusTodoHomeFragmentDirections.actionFocusTodoFragmentToSearchFocusFragment())
        }

        mBinding.btnReset.setOnClickListenerBlock {
            viewModel.resetState()
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