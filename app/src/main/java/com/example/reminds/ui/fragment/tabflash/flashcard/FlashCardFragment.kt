package com.example.reminds.ui.fragment.tabflash.flashcard

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentFlashCardBinding

class FlashCardFragment : BaseFragment<FragmentFlashCardBinding>() {
    override fun getViewBinding(): FragmentFlashCardBinding {
        return FragmentFlashCardBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
    }

    private fun setupLayout() {
        mBinding.recyclerFlashCard.layoutManager = GridLayoutManager(requireActivity(), 2)

    }
}