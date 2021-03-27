package com.example.reminds.ui.fragment.createtopic

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.common.base.model.TopicGroupEntity.Companion.NULL_IN_DB
import com.example.reminds.R
import com.example.reminds.databinding.FragmentBsCreateTopicBinding
import com.example.reminds.ui.adapter.IconTopicAdapter
import com.example.reminds.utils.checkDiffAndSetText
import com.example.reminds.utils.setOnClickListenerBlock
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_option_for_work_bs.*

@AndroidEntryPoint
class CreateTopicBSFragment : BottomSheetDialogFragment() {
    private val args by navArgs<CreateTopicBSFragmentArgs>()

    private val viewModel: CreateTopicBSViewModel by viewModels()

    lateinit var mBinding: FragmentBsCreateTopicBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBsCreateTopicBinding.inflate(inflater)

        if (args.idTopic != NULL_IN_DB) {
            viewModel.postIdGroup(args.idTopic)
        }
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
        mBinding.edtInput.doAfterTextChanged {
            viewModel.setNameTopicTemp(it.toString())
            when (it.toString().isNotBlank()) {
                true -> {
                    mBinding.btnSave.isClickable = true
                    btnSave.setTextColor(requireContext().resources.getColor(R.color.blue_900))
                }
                false -> {
                    mBinding.btnSave.isClickable = false
                    btnSave.setTextColor(requireContext().resources.getColor(R.color.bg_gray))
                }
            }
        }
        mBinding.btnSave.setOnClickListenerBlock {
            viewModel.insertTopicToDatabase()
            Handler().postDelayed({
                dismissAllowingStateLoss()
            }, 500)
        }

        mBinding.btnBack.setOnClickListenerBlock {
            dismiss()
        }
    }


    private fun setupLayout() {
        mBinding.recyclerIcon.layoutManager = GridLayoutManager(requireContext(), 6)
        IconTopicAdapter {
            viewModel.setIconResourceTopicTemp(it)
        }.apply {
            mBinding.recyclerIcon.adapter = this
        }
    }

    private fun setupObserver() {
        viewModel.listIconLiveData.observe(viewLifecycleOwner, {
            (mBinding.recyclerIcon.adapter as? IconTopicAdapter)?.submitList(it.toList())
        })

        viewModel.topicGroup.observe(viewLifecycleOwner, { topic ->
            mBinding.edtInput.checkDiffAndSetText(topic.name)
            topic.iconResource?.let {
                (mBinding.recyclerIcon.adapter as? IconTopicAdapter)
                    ?.itemChangeSelected(viewModel.findPositionIcon(it))
            }
        })
    }
}