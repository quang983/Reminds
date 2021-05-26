package com.example.reminds.ui.fragment.tabdaily.detail

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentDailyTaskDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyTaskDetailFragment : BaseFragment<FragmentDailyTaskDetailBinding>() {
    override fun getViewBinding(): FragmentDailyTaskDetailBinding {
        return FragmentDailyTaskDetailBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupCalendarView()
    }

    private fun setupLayout() {

    }

    private fun setupCalendarView() {
        val dm = DisplayMetrics()
        val display = activity?.display
        display?.getRealMetrics(dm)
    }
}