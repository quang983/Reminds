package com.example.reminds.ui.fragment.createtopic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reminds.databinding.FragmentBsCreateTopicBinding
import com.example.reminds.ui.adapter.IconTopicAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTopicBSFragment : BottomSheetDialogFragment() {
    private val viewModel: CreateTopicBSViewModel by viewModels()

    lateinit var mBinding: FragmentBsCreateTopicBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBsCreateTopicBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupObserver()
        setupListener()
    }

    private fun setupListener() {
        mBinding.edtInput.requestFocus()
        mBinding.edtInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mBinding.edtInput.clearFocus()
            }
            false
        }
    }


    private fun setupLayout() {
        mBinding.recyclerIcon.layoutManager = GridLayoutManager(requireContext(), 6)
        IconTopicAdapter {

        }.apply {
            mBinding.recyclerIcon.adapter = this
        }
    }

    private fun setupObserver() {
        viewModel.listIconLiveData.observe(viewLifecycleOwner, {
            (mBinding.recyclerIcon.adapter as? IconTopicAdapter)?.submitList(it.toList())
        })
    }
}