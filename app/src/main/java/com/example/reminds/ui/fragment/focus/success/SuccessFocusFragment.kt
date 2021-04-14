package com.example.reminds.ui.fragment.focus.success

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentSuccessFocusBinding
import com.example.reminds.utils.navigateUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessFocusFragment : BaseFragment<FragmentSuccessFocusBinding>() {
    override fun getViewBinding(): FragmentSuccessFocusBinding {
        return FragmentSuccessFocusBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}