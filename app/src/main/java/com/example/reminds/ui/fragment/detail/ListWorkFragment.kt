package com.example.reminds.ui.fragment.detail

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.ui.adapter.ListContentCheckAdapter
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.utils.navigateUp
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list_work.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class ListWorkFragment : Fragment() {
    private val args by navArgs<ListWorkFragmentArgs>()

    private val viewModel: ListWorkViewModel by viewModels()
    private lateinit var adapter: ListWorkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getListWork(args.idGroup)
        return inflater.inflate(R.layout.fragment_list_work, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUI()
        observeData()
        setupListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.insertWorksObject(getListWorkAdapter())
    }

    private fun setupListener() {
        extendedFab.setOnClickListener {
            showDialogInputWorkTopic()
        }
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        activity?.actionBar?.setTitle(R.string.home_screen_title)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings_task_view -> {

            }
            android.R.id.home -> {
                navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        adapter = ListWorkAdapter(onClickTitle = { workPosition ->
            viewModel.updateListWork(getListWorkAdapter(), workPosition)
        }, insertContentToWork = { content, contentPosition, position ->
            viewModel.updateAndAddContent(content, contentPosition, position)
        }, handlerCheckItem = { content, position ->
            viewModel.handlerCheckItem(content, position)
        }, updateNameContent = { content, position ->
            viewModel.updateNameContent(content, position)
        }, moreActionClick = { item, type, wPosition ->
            when (type) {
                ListContentCheckAdapter.TYPE_TIMER_CLICK -> {
                    setupTimePickerForContent(item, wPosition)
                }
                ListContentCheckAdapter.TYPE_TAG_CLICK -> {
                    viewModel.hashTagContent(item, wPosition)
                }
                ListContentCheckAdapter.TYPE_DELETE_CLICK -> {
                    viewModel.deleteContent(item, wPosition)
                }
            }
        }).apply {
            recyclerWorks.adapter = this
        }
    }

    private fun observeData() {
        with(viewModel) {
            listWorkData.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
        }
    }

    private fun showDialogInputWorkTopic() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thêm mới")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val text = input.text.toString()
            viewModel.insertWorkInCurrent(text)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun setupTimePickerForContent(item: ContentDataEntity, workPosition: Int) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Chọn thời gian")
            .build()
        picker.show(childFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            val newHour: Int = picker.hour
            val newMinute: Int = picker.minute
            val longTimer = newHour * 60 + newMinute
            item.timer = longTimer.toLong()
            viewModel.setTimerContent(item, workPosition)
        }
        picker.addOnNegativeButtonClickListener {
        }
        picker.addOnCancelListener {
        }
        picker.addOnDismissListener {
        }
    }

    private fun getListWorkAdapter(): List<WorkDataEntity> {
        return adapter.currentList
    }

    override fun onResume() {
        super.onResume()
        catchEventKeyboard()
    }

    private fun catchEventKeyboard() {
        /*KeyboardVisibilityEvent.setEventListener(
            requireActivity(),
            viewLifecycleOwner,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    when (isOpen) {
                        false -> {

                        }
                    }
                }
            })*/
    }
}