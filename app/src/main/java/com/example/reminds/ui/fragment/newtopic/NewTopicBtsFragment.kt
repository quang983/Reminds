package com.example.reminds.ui.fragment.newtopic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.domain.model.TopicGroupEntity
import com.example.reminds.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_topic_bts.*

@AndroidEntryPoint
class NewTopicBtsFragment : BottomSheetDialogFragment() {
    private val viewModel: NewTopicBtsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_topic_bts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
    }

    private fun setListener() {
        btnDone.setOnClickListener {
            viewModel.insertTopic(
                TopicGroupEntity(
                    System.currentTimeMillis(),
                    edtTopic.text.toString()
                )
            )
            dismiss()
        }

        btnDatePicker.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnCancelListener {

        }
        picker.addOnNegativeButtonClickListener {

        }
    }
}