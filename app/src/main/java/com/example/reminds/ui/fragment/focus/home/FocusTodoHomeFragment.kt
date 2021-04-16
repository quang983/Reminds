package com.example.reminds.ui.fragment.focus.home

import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentHomeFocusBinding
import com.example.reminds.service.timer.HelloService
import com.example.reminds.service.timer.HelloService.Companion.MESSAGE_CANCEL_NOTIFICATION
import com.example.reminds.service.timer.HelloService.Companion.MESSAGE_PAUSE_NOTIFICATION
import com.example.reminds.ui.fragment.focus.dialogtimer.DialogTimerFragment
import com.example.reminds.ui.sharedviewmodel.FocusActivityViewModel
import com.example.reminds.utils.gone
import com.example.reminds.utils.navigate
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import www.sanju.motiontoast.MotionToast

@AndroidEntryPoint
class FocusTodoHomeFragment : BaseFragment<FragmentHomeFocusBinding>() {
    private val viewModelShared: FocusActivityViewModel by activityViewModels()

    lateinit var animator: ValueAnimator

    private var _serviceMessenger: Messenger? = null

    private var bound: Boolean = false

    companion object {
        const val RESULTS_MINUTES_PICKER = "RESULTS_MINUTES_PICKER"
    }

    override fun getViewBinding(): FragmentHomeFocusBinding {
        return FragmentHomeFocusBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResult()
        Intent(requireActivity(), HelloService::class.java).let { intent ->
            requireActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unbindService(mConnection)
    }

    private fun setFragmentResult() {
        setFragmentResultListener(RESULTS_MINUTES_PICKER) { _, bundle ->
            viewModelShared.setTimerData(bundle.getLong(DialogTimerFragment.EXTRAS_MINUTES_DATA))
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

        mBinding.btnStart.setOnClickListener {
            if (viewModelShared.timerRunningStateLiveData.value == STATE.INDIE || viewModelShared.timerRunningStateLiveData.value == STATE.PAUSE) {
                startTimer()
            } else {
                pauseTimer()
            }
        }
    }

    private fun startTimer() {
        animator.duration = viewModelShared.mTimeLeftInMillis
        viewModelShared.startTimer()
        sendActionInsertAlert(HelloService.MESSAGE_PLAY_NOTIFICATION, viewModelShared.mTimeLeftInMillis)
        if (animator.isStarted) {
            animator.resume()
        } else {
            animator.start()
        }
    }

    private fun pauseTimer() {
        viewModelShared.pauseTimer()
        animator.pause()
        sendActionInsertAlert(MESSAGE_PAUSE_NOTIFICATION, viewModelShared.mTimeLeftInMillis)
    }

    private fun setupObserver() {
        viewModelShared.timeShowLiveData.observe(viewLifecycleOwner, {
            mBinding.tvTime.text = it.toString()
        })

        viewModelShared.itemWorkSelected.observe(viewLifecycleOwner, {
            it?.let {
                mBinding.groupTask.visible()
                mBinding.tvWorkName.text = it.name
            } ?: let {
                mBinding.groupTask.gone()
            }
        })

        viewModelShared.timerRunningStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                STATE.INDIE -> {
                    animator.cancel()
                    mBinding.btnReset.gone()
                    sendActionInsertAlert(MESSAGE_CANCEL_NOTIFICATION, viewModelShared.mTimeLeftInMillis)
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
                    animator.cancel()
                    mBinding.btnReset.gone()
                    mBinding.btnStart.text = "Start"
                    viewModelShared.resetState()
                    viewModelShared.doneAllInWork()
                    sendActionInsertAlert(MESSAGE_CANCEL_NOTIFICATION, viewModelShared.mTimeLeftInMillis)
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
            if (viewModelShared.timerRunningStateLiveData.value == STATE.INDIE) {
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
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.notify_title))
                .setMessage(resources.getString(R.string.alert_cancel_focus))
                .setNegativeButton(resources.getString(R.string.cancel_action)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.accept_action)) { _, _ ->
                    viewModelShared.resetState()
                }
                .show()
        }

        mBinding.btnClose.setOnClickListenerBlock {
            viewModelShared.itemWorkSelected.postValue(null)
        }
    }

    private fun sendActionInsertAlert(key: Int, data: Long) {
        if (!bound) return

        val msg: Message = Message.obtain(null, key, data)
        try {
            _serviceMessenger?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
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

    /**
     * Class for interacting with the main interface of the service.
     */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            _serviceMessenger = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            _serviceMessenger = null
            bound = false
        }
    }

}