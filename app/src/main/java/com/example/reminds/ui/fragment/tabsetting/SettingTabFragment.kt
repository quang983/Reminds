package com.example.reminds.ui.fragment.tabsetting

import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentSettingTabBinding

class SettingTabFragment : BaseFragment<FragmentSettingTabBinding>() {
    override fun getViewBinding(): FragmentSettingTabBinding {
        return FragmentSettingTabBinding.inflate(layoutInflater)
    }
}