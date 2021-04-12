package com.example.reminds.ui.fragment.focus.dialogtimer

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.reminds.common.BaseDialogFullsizeFragment
import com.example.reminds.common.OnSnapPositionChangeListener
import com.example.reminds.common.SnapOnScrollListener
import com.example.reminds.databinding.FragmentDialogTimerPickerBinding
import com.example.reminds.ui.adapter.TimerPickerAdapter
import com.example.reminds.ui.fragment.focus.home.FocusTodoHomeFragment.Companion.RESULTS_MINUTES_PICKER
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.attachSnapHelperWithListener
import com.example.reminds.utils.getSnapPosition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogTimerFragment : BaseDialogFullsizeFragment<FragmentDialogTimerPickerBinding>() {
    private val viewModel: DialogTimerViewModel by viewModels()

    companion object {
        const val EXTRAS_MINUTES_DATA = "EXTRAS_MINUTES_DATA"
    }

    override fun getViewBinding(): FragmentDialogTimerPickerBinding {
        return FragmentDialogTimerPickerBinding.inflate(layoutInflater)
    }

    override fun setupLayout() {
        val snapHelper = LinearSnapHelper()
        mBinding.recyclerTimer.layoutManager = LinearLayoutManager(requireContext())
        snapHelper.getSnapPosition(mBinding.recyclerTimer)
        mBinding.recyclerTimer.attachSnapHelperWithListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                viewModel.minuteSelectedItem = TimestampUtils
                    .convertMinutesToMiliTime((mBinding.recyclerTimer.adapter as? TimerPickerAdapter)?.currentList?.get(position) ?: 0)
            }
        })
        TimerPickerAdapter().apply {
            mBinding.recyclerTimer.adapter = this
        }
        mBinding.tvApply.setOnClickListener {
            setFragmentResult(RESULTS_MINUTES_PICKER, bundleOf(EXTRAS_MINUTES_DATA to viewModel.minuteSelectedItem))
            dismiss()
        }
    }

    override fun setupObserver() {
        viewModel.listMinuteItemView.observe(viewLifecycleOwner) {
            (mBinding.recyclerTimer.adapter as? TimerPickerAdapter)?.submitList(it)
        }
    }
}