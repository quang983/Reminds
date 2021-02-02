package com.example.reminds.ui.fragment.newtopic

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.reminds.R
import com.example.reminds.utils.KeyboardUtils
import com.example.reminds.utils.setTextChangedListener
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
        setupUI()
        setListener()
    }

    private fun setupUI() {
        edtTopic.requestFocus()
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            KeyboardUtils.showKeyboard(requireContext())
        }, 500)
    }

    private fun setListener() {
        btnDone.setOnClickListener {
            if (edtTopic.text.toString().isNotBlank()) {
                viewModel.insertTopic(
                    edtTopic.text.toString()
                )
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Tiêu đề phải có tối thiểu 1 ký tự!", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            dismiss()
        }

        /* btnDatePicker.setOnClickListener {
             showDatePicker()
         }*/

        edtTopic.setTextChangedListener {
            if (it.text.isNotBlank()) {
                btnDone.setTextColor(requireContext().resources.getColor(R.color.blue_700))
            } else {
                btnDone.setTextColor(requireContext().resources.getColor(R.color.bg_gray))
            }
        }
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnCancelListener {

        }
        picker.addOnPositiveButtonClickListener {
        }
    }
}