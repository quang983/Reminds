package com.example.reminds.ui.fragment.focus.dialogtimer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.reminds.common.BaseDialogFragment
import com.example.reminds.common.OnSnapPositionChangeListener
import com.example.reminds.common.SnapOnScrollListener
import com.example.reminds.databinding.FragmentDialogTimerPickerBinding
import com.example.reminds.ui.adapter.TimerPickerAdapter
import com.example.reminds.utils.attachSnapHelperWithListener
import com.example.reminds.utils.getSnapPosition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogTimerFragment : BaseDialogFragment<FragmentDialogTimerPickerBinding>() {
    private val viewModel: DialogTimerViewModel by viewModels()

    override fun getViewBinding(): FragmentDialogTimerPickerBinding {
        return FragmentDialogTimerPickerBinding.inflate(layoutInflater)
    }

    override fun setupLayout() {
        val snapHelper = LinearSnapHelper()
        mBinding.recyclerTimer.layoutManager = LinearLayoutManager(requireContext())
        snapHelper.getSnapPosition(mBinding.recyclerTimer)
        mBinding.recyclerTimer.attachSnapHelperWithListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                Log.d("taskist", "onSnapPositionChange: $position")
            }
        })
        TimerPickerAdapter().apply {
            mBinding.recyclerTimer.adapter = this
        }
    }

    override fun setupObserver() {
        viewModel.listMinuteItemView.observe(viewLifecycleOwner, {
            (mBinding.recyclerTimer.adapter as? TimerPickerAdapter)?.submitList(it)
        })
    }
}