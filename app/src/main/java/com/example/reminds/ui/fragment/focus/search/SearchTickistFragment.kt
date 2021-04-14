package com.example.reminds.ui.fragment.focus.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentSearchTickistBinding
import com.example.reminds.ui.adapter.ListSearchAdapter
import com.example.reminds.ui.sharedviewmodel.FocusActivityViewModel
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.navigateUp
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchTickistFragment : BaseFragment<FragmentSearchTickistBinding>() {
    private val  viewModelShared: FocusActivityViewModel by activityViewModels()
    private val viewModel: SearchTickistViewModel by viewModels()

    override fun getViewBinding(): FragmentSearchTickistBinding {
        return FragmentSearchTickistBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupLayout()
        setupObserver()
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

    private fun setupLayout() {
        mBinding.recyclerSearch.adapter = ListSearchAdapter(viewModelShared.itemWorkSelected.value?.id ?: -1) {
            viewModelShared.itemWorkSelected.postValue(it)
            navigateUp()
        }
    }

    private fun setupObserver() {
        viewModel.getAllWorkLiveData.observe(viewLifecycleOwner, {
            (mBinding.recyclerSearch.adapter as? ListSearchAdapter)?.submitList(it)
        })
    }
}