package com.example.reminds.ui.fragment.tabdaily.list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentDailyListBinding
import com.example.reminds.ui.adapter.DailyListAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DailyListFragment : BaseFragment<FragmentDailyListBinding>() {
    override fun getViewBinding(): FragmentDailyListBinding {
        return FragmentDailyListBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.recyclerList.adapter = DailyListAdapter()
        mBinding.recyclerList.layoutManager = GridLayoutManager(requireContext(), 2)
    }
}