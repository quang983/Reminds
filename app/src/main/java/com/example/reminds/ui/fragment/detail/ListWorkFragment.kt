package com.example.reminds.ui.fragment.detail

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.ContentDataEntity
import com.example.reminds.R
import com.example.reminds.ui.adapter.ListContentCheckAdapter
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list_work.*
import kotlinx.android.synthetic.main.layout_custom_alert_text_input.view.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class ListWorkFragment : Fragment() {
    private val args by navArgs<ListWorkFragmentArgs>()

    private val viewModel: ListWorkViewModel by viewModels()
    private val homeSharedViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ListWorkAdapter
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View

    companion object {
        const val FRAGMENT_RESULT_TIMER = "FRAGMENT_RESULT_TIMER"
    }

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

    private fun setupListener() {
        extendedFab.setOnClickListener {
            customAlertDialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_custom_alert_text_input, null, false)
            customAlertDialogView.setPadding(36.toDp, 0, 36.toDp, 0)
            showDialogInputWorkTopic()
        }
        rootWork.setOnClickListener {
            if (homeSharedViewModel.isKeyboardShow.value == true) {
                viewModel.reSaveListWorkAndCreateStateFocus()
                hideSoftKeyboard()
            } else {
                viewModel.reSaveListWorkToDb(viewModel.listWorkViewModel.size - 1)

            }
        }
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        activity?.actionBar?.setTitle(R.string.home_screen_title)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (args.idGroup != 1L) {
            inflater.inflate(R.menu.top_app_bar, menu)
        } else {
            inflater.inflate(R.menu.menu_main, menu)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings_task_view -> {

                viewModel.getListWork(args.idGroup)
              /*  viewModel.isShowDone.postValue(viewModel.isShowDone.value ?: false)
                if (viewModel.isShowDone.value == false) {
                    item.setTitle("Hiển thị lời nhắc đã hoàn tất")
                } else {
                    item.setTitle("Ẩn lời nhắc đã hoàn tất")
                }*/
            }
            android.R.id.home -> {
                navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        adapter = ListWorkAdapter(onClickTitle = { workPosition ->
            if (homeSharedViewModel.isKeyboardShow.value == true) {
                hideSoftKeyboard()
            } else {
                viewModel.reSaveListWorkToDb(workPosition)
            }
            viewModel.workPositionSelected = workPosition
        }, insertContentToWork = { content, position ->
            viewModel.updateAndAddContent(content, position)
        }, handlerCheckItem = { content, position ->
            viewModel.handlerCheckedContent(content, position)
        }, updateNameContent = { content, position ->
            viewModel.updateContentData(content, position)
        }, moreActionClick = { item, type, wPosition ->
            when (type) {
                ListContentCheckAdapter.TYPE_TIMER_CLICK -> {
                    setupTimePickerForContent(item, wPosition)
                }
                ListContentCheckAdapter.TYPE_TAG_CLICK -> {
                    viewModel.updateContentData(item, wPosition)
                }
                ListContentCheckAdapter.TYPE_DELETE_CLICK -> {
                    showAlertDeleteDialog(item, wPosition)
                }
            }
        }).apply {
            recyclerWorks.adapter = this
        }
        homeSharedViewModel.isKeyboardShow.observe(viewLifecycleOwner, {
            if (!it) {
                viewModel.reSaveListWorkAndCreateStateFocus()
            }
        })
    }


    private fun observeData() {
        with(viewModel) {
            listWorkData.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
        }
    }

    private fun showAlertDeleteDialog(content: ContentDataEntity, wPosition: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.notify_title))
            .setMessage(resources.getString(R.string.content_delete_topic_title))
            .setNegativeButton(resources.getString(R.string.cancel_action)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.accept_action)) { _, _ ->
                viewModel.deleteContent(content, wPosition)
            }
            .show()
    }


    private fun showDialogInputWorkTopic() {
        /*  val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
          builder.setTitle(requireContext().getText(R.string.new_data_title))
          val input = EditText(requireContext())
          input.inputType = InputType.TYPE_CLASS_TEXT
          builder.setView(input)
          builder.setPositiveButton("OK") { _, _ ->
              val text = input.text.toString()
              viewModel.insertNewWork(text)
          }
          builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

          builder.show()*/

        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle("Thêm mới")
            .setPositiveButton("Thêm") { dialog, _ ->
                customAlertDialogView.edtInput.text.toString().takeIf { it.isNotBlank() }?.let {
                    viewModel.insertNewWork(it)
                } ?: Toast.makeText(requireContext(), "Tiêu đề phải có tối thiểu 1 ký tự!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Huỷ") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        customAlertDialogView.requestFocus()

        Handler().postDelayed({
            KeyboardUtils.showKeyboard(requireContext())
        }, 500)
    }

    private fun setupTimePickerForContent(item: ContentDataEntity, workPosition: Int) {
        navigate(ListWorkFragmentDirections.actionSecondFragmentToDateTimePickerDialog(System.currentTimeMillis()))
        setFragmentResultListener(FRAGMENT_RESULT_TIMER) { _, bundle ->
            item.timer = bundle.getLong(TIME_PICKER_BUNDLE)
            viewModel.updateContentData(item, workPosition)
            homeSharedViewModel.notifyDataInsert.postValue(
                AlarmNotificationEntity(
                    TimestampUtils.getFullFormatTime(item.timer), item.idOwnerWork, item.id, item.name, "Thông báo"
                )
            )
        }
        /*   val picker = MaterialTimePicker.Builder()
               .setTimeFormat(TimeFormat.CLOCK_24H)
               .setHour(12)
               .setMinute(0)
               .setTitleText(requireContext().getText(R.string.select_timer_title))
               .build()
           picker.show(childFragmentManager, "tag")

           picker.addOnPositiveButtonClickListener {
               picker.dismiss()
               val newHour: Int = picker.hour
               val newMinute: Int = picker.minute
               val longTimer = newHour * 60 + newMinute
               item.timer = longTimer.toLong()
               Handler().postDelayed({
                   viewModel.updateContentData(item, workPosition)
               }, 500)
           }
           picker.addOnNegativeButtonClickListener {
           }
           picker.addOnCancelListener {
           }
           picker.addOnDismissListener {
           }*/
    }
}