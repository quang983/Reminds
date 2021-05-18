package com.example.reminds.ui.fragment.tabdaily.add

import android.os.Bundle
import android.view.View
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentAddDailyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddDailyTaskFragment : BaseFragment<FragmentAddDailyBinding>() {
    override fun getViewBinding(): FragmentAddDailyBinding {
        return FragmentAddDailyBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}