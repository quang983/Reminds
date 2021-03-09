package com.example.reminds.ui.fragment.detail

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.content.Context
import android.content.DialogInterface
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
import kotlinx.android.synthetic.main.layout_empty_animation.view.*


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
        extendedFab.setOnClickListenerBlock {
            showDialogInputWorkTopic()
        }
        rootWork.setOnClickListenerBlock {
            if (homeSharedViewModel.isKeyboardShow.value == true) {
                viewModel.reSaveListWorkAndCreateStateFocus()
                hideSoftKeyboard()
            } else {
                viewModel.listWorkViewModel.lastOrNull()?.id?.let {
                    viewModel.reSaveListWorkToDb(it)
                }
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
        adapter = ListWorkAdapter(onClickTitle = { wId ->
            if (homeSharedViewModel.isKeyboardShow.value == true) {
                hideSoftKeyboard()
            } else {
                viewModel.reSaveListWorkToDb(wId)
            }
        }, insertContentToWork = { content, wId ->
            viewModel.updateAndAddContent(content, wId)
        }, handlerCheckItem = { content, position ->
            viewModel.handlerCheckedContent(content, position)
            showAds()
        }, updateNameContent = { content, wId ->
            viewModel.updateContentData(content, wId)
        }, moreActionClick = { item, type, wId ->
            when (type) {
                ListContentCheckAdapter.TYPE_TIMER_CLICK -> {
                    setupTimePickerForContent(item, wId)
                }
                ListContentCheckAdapter.TYPE_TAG_CLICK -> {
                    viewModel.updateContentData(item, wId)
                }
                ListContentCheckAdapter.TYPE_DELETE_CLICK -> {
                    showAlertDeleteDialog(resources.getString(R.string.content_delete_topic_title)) {
                        viewModel.deleteContent(item, wId)
                    }
                }
            }
        }, deleteWorkClick = {
            showAlertDeleteDialog(resources.getString(R.string.message_alert_delete_work_title)) {
                viewModel.deleteWork(it)
            }
        }, handlerCheckedAll = { workId, doneAll ->
            viewModel.handleDoneAllContentFromWork(workId, doneAll)
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

            listWorkData.observe(viewLifecycleOwner, { it ->
                when {
                    it.isEmpty() -> {
                        layoutEmpty.visible()
                        layoutEmpty.imgIconAnimation.setAnimation(R.raw.empty_card)
                        layoutEmpty.imgIconAnimation.repeatCount = 10
                        layoutEmpty.imgIconAnimation.loop(true)
                        layoutEmpty.imgIconAnimation.playAnimation()
                        layoutEmpty.tvEmptyAnimation.text = resources.getString(R.string.empty_list)
                    }
                    it.sumByDouble { it.listContent.size.toDouble() }.toInt() == 0 && !checkFirstTapTap() -> {
                        layoutEmpty.visible()
                        layoutEmpty.imgIconAnimation.setAnimation(R.raw.tap_tap)
                        layoutEmpty.imgIconAnimation.repeatCount = 10
                        layoutEmpty.imgIconAnimation.loop(true)
                        layoutEmpty.imgIconAnimation.playAnimation()
                        layoutEmpty.tvEmptyAnimation.text = resources.getString(R.string.tap_tap)
                        val shared = requireActivity().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
                        shared.edit().putBoolean(CacheImpl.KEY_FIRST_TAP_TAP, true).apply()
                    }
                    else -> {
                        layoutEmpty.gone()
                    }
                }
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
        customAlertDialogView.rootView.textInput.hint = getString(R.string.add_new_work_hint)
        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(resources.getString(R.string.new_data_title))
            .setPositiveButton(resources.getString(R.string.add)) { _, _ ->
                customAlertDialogView.edtInput.text.toString().takeIf { it.isNotBlank() }?.let {
                    viewModel.insertNewWork(it)
                } ?: Toast.makeText(requireContext(), resources.getString(R.string.warning_title_min), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show().apply {
                this.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
                this.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
            }
        customAlertDialogView.requestFocus()
        Handler().postDelayed({
            KeyboardUtils.showKeyboard(requireContext())
        }, 500)
    }

    private fun setupTimePickerForContent(item: ContentDataEntity, wId: Long) {
        navigate(ListWorkFragmentDirections.actionSecondFragmentToDateTimePickerDialog(System.currentTimeMillis()))
        setFragmentResultListener(FRAGMENT_RESULT_TIMER) { _, bundle ->
            item.timer = bundle.getLong(TIME_PICKER_BUNDLE)
            viewModel.updateContentData(item, wId)
            homeSharedViewModel.notifyDataInsert.postValue(
                AlarmNotificationEntity(
                    item.timer, item.idOwnerWork, item.id, item.name, resources.getString(R.string.notify_title)
                )
            )
        }
    }

    private fun showAds() {
        val shared = requireContext().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        var sum = shared.getInt(KEY_SUM_DONE_TASK, 0)
        if (sum < 10) {
            sum += 1
            shared.edit().putInt(KEY_SUM_DONE_TASK, sum).apply()
        } else {
            shared.edit().putInt(KEY_SUM_DONE_TASK, 0).apply()
            homeSharedViewModel.showAdsMobile.postValue(true)
        }
    }

    private fun checkFirstTapTap(): Boolean {
        val shared = requireActivity().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        return shared.getBoolean(CacheImpl.KEY_FIRST_TAP_TAP, false)
    }

}