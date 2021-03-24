package com.example.reminds.ui.fragment.focus.list

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.common.SpacesItemDecoration
import com.example.reminds.databinding.FragmentListFocusBinding

class ListFocusFragment : BaseFragment<FragmentListFocusBinding>() {
    override fun getViewBinding(): FragmentListFocusBinding {
        return FragmentListFocusBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        mBinding.recyclerListFocus.layoutManager = GridLayoutManager(requireActivity(), 2)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen._6sdp)
        mBinding.recyclerListFocus.addItemDecoration(SpacesItemDecoration(spacingInPixels))
    }
}