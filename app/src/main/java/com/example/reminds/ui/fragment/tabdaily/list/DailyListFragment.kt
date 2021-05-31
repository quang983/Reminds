package com.example.reminds.ui.fragment.tabdaily.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentDailyListBinding
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.ui.adapter.DailyListAdapter
import com.example.reminds.utils.navigate
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar


@AndroidEntryPoint
class DailyListFragment : BaseFragment<FragmentDailyListBinding>() {
    private val _dailyListViewModel by viewModels<DailyListViewModel>()

    private lateinit var adapter: DailyListAdapter

    override fun getViewBinding(): FragmentDailyListBinding {
        return FragmentDailyListBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupObserve()
        setupListener()
    }

    private fun setupLayout() {
        adapter = DailyListAdapter {
            navigate(DailyListFragmentDirections.actionDailyTabFragmentToDetailDailyFragment(0))
        }
        mBinding.recyclerList.adapter = adapter
        mBinding.recyclerList.layoutManager = GridLayoutManager(requireContext(), 2)

        (requireActivity() as? MainActivity)?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.visible()

    }

    private fun setupListener() {
        mBinding.btnAddTask.setOnClickListenerBlock {
            navigate(DailyListFragmentDirections.actionDailyTabFragmentToAddDailyFragment())
        }
    }

    private fun setupObserve() {
        _dailyListViewModel.dailyTaskWithDivider.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}