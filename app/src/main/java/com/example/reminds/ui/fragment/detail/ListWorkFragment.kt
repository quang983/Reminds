package com.example.reminds.ui.fragment.detail

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.ContentDataEntity
import com.example.framework.local.cache.CacheImpl
import com.example.framework.local.cache.CacheImpl.Companion.KEY_SUM_DONE_TASK
import com.example.reminds.R
import com.example.reminds.common.Constants.ERROR_LOG
import com.example.reminds.ui.adapter.ListContentCheckAdapter
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
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
    private lateinit var menuToolbar: Menu

    companion object {
        const val FRAGMENT_RESULT_TIMER = "FRAGMENT_RESULT_TIMER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Set up MaterialContainerTransform transition as sharedElementEnterTransition.
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
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
        activity?.actionBar?.title = args.titleGroup
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menuToolbar = menu
        if (args.idGroup != 1L) {
            inflater.inflate(R.menu.top_app_bar, menu)
        } else {
            inflater.inflate(R.menu.menu_main, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings_task_view -> {
                val isShowDone = viewModel.isShowDoneLiveData.value ?: true
                viewModel.saveTopicGroup(!isShowDone)
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
            showAds()
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
                    showAlertDeleteDialog(resources.getString(R.string.content_delete_topic_title)) {
                        viewModel.deleteContent(item, wPosition)
                    }
                }
            }
        }, hideWorkClick = {
            showAlertDeleteDialog(resources.getString(R.string.message_alert_hide_work_title)) {
            }

        }, deleteWorkClick = {
            showAlertDeleteDialog(resources.getString(R.string.message_alert_delete_work_title)) {
                viewModel.deleteWork(it)
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
            isShowDoneLiveData.observe(viewLifecycleOwner, {
                try {
                    if (!it) {
                        menuToolbar.findItem(R.id.action_settings_task_view).title = getString(R.string.title_show_content)
                    } else {
                        menuToolbar.findItem(R.id.action_settings_task_view).title = getString(R.string.title_hide_content)
                    }
                } catch (e: Throwable) {
                    Log.e(ERROR_LOG, e.message.toString())
                }
            })
        }
    }

    private fun showAlertDeleteDialog(message: String, block: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.notify_title))
            .setMessage(message)
            .setNegativeButton(resources.getString(R.string.cancel_action)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.accept_action)) { _, _ ->
                block.invoke()
            }
            .show()
    }

    private fun showDialogInputWorkTopic() {
        customAlertDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_custom_alert_text_input, null, false)
        customAlertDialogView.setPadding(36.toDp, 0, 36.toDp, 0)
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
                    item.timer, item.idOwnerWork, item.id, item.name, "Thông báo"
                )
            )
        }
    }

    private fun showAds() {
        val shared = requireContext().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        var sum = shared.getInt(KEY_SUM_DONE_TASK, 0)
        if (sum < 5) {
            sum += 1
            shared.edit().putInt(KEY_SUM_DONE_TASK, sum).apply()
        } else {
            shared.edit().putInt(KEY_SUM_DONE_TASK, 0).apply()
            homeSharedViewModel.showAdsMobile.postValue(true)
        }
    }
}