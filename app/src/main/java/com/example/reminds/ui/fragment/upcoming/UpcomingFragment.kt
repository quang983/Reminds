package com.example.reminds.ui.fragment.upcoming

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.ContentDataEntity
import com.example.framework.local.cache.CacheImpl
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentUpcomingBinding
import com.example.reminds.ui.adapter.ListContentCheckAdapter
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.ui.fragment.detail.ListWorkFragmentDirections
import com.example.reminds.ui.fragment.detail.ListWorkViewModel
import com.example.reminds.ui.fragment.setting.WorksSettingFragment
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_custom_alert_text_input.view.*
import org.joda.time.DateTime
import java.util.*

@AndroidEntryPoint
class UpcomingFragment : BaseFragment<FragmentUpcomingBinding>() {
    override fun getViewBinding(): FragmentUpcomingBinding {
        return FragmentUpcomingBinding.inflate(layoutInflater)
    }

    private val viewModel: ListWorkViewModel by viewModels()
    private val homeSharedViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ListWorkAdapter
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View

    private var mGroupId: Long = 0

    companion object {
        const val FRAGMENT_RESULT_TIMER = "FRAGMENT_RESULT_TIMER"
        const val FRAGMENT_SETTING_OPTION = "FRAGMENT_SETTING_OPTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
        calGroupId()
        TimestampUtils.getDate(System.currentTimeMillis())
        viewModel.getListWork(mGroupId)
        setFragmentResultListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupLayout()
        observeData()
        setupListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    private fun calGroupId() {
        mGroupId = TimestampUtils.getLongTimeFromStr(Calendar.getInstance().timeInMillis)
    }

    private fun setupListener() {
        mBinding.extendedFab.setOnClickListenerBlock {
            showDialogInputWorkTopic()
        }
        mBinding.rootWork.setOnClickListenerBlock {
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                navigate(ListWorkFragmentDirections.actionSecondFragmentToSettingFragment(idGroup = mGroupId))
            }
            android.R.id.home -> {
                navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        adapter = ListWorkAdapter(
            onClickTitle = { wId ->
                if (homeSharedViewModel.isKeyboardShow.value == true) {
                    hideSoftKeyboard()
                } else {
                    viewModel.updateWorkChange(wId, true)
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
            }, updateDataChanged = {
                if (homeSharedViewModel.isKeyboardShow.value == true) {
                    hideSoftKeyboard()
                } else {
                    viewModel.updateWorkChange(it, false)
                }
            }, intoSettingFragment = {
                navigate(ListWorkFragmentDirections.actionSecondFragmentToOptionForWorkBSFragment(it.id))
            }).apply {
            mBinding.recyclerWorks.adapter = this
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
                        mBinding.layoutEmpty.root.visible()
                        mBinding.layoutEmpty.imgIconAnimation.setAnimation(R.raw.empty_card)
                        mBinding.layoutEmpty.imgIconAnimation.repeatCount = 10
                        mBinding.layoutEmpty.imgIconAnimation.loop(true)
                        mBinding.layoutEmpty.imgIconAnimation.playAnimation()
                        mBinding.layoutEmpty.tvEmptyAnimation.text = resources.getString(R.string.empty_list)
                    }
                    it.sumByDouble { it.listContent.size.toDouble() }.toInt() == 0 && !checkFirstTapTap() -> {
                        mBinding.layoutEmpty.root.visible()
                        mBinding.layoutEmpty.imgIconAnimation.setAnimation(R.raw.tap_tap)
                        mBinding.layoutEmpty.imgIconAnimation.repeatCount = 10
                        mBinding.layoutEmpty.imgIconAnimation.loop(true)
                        mBinding.layoutEmpty.imgIconAnimation.playAnimation()
                        mBinding.layoutEmpty.tvEmptyAnimation.text = resources.getString(R.string.tap_tap)
                        val shared = requireActivity().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
                        shared.edit().putBoolean(CacheImpl.KEY_FIRST_TAP_TAP, true).apply()
                    }
                    else -> {
                        mBinding.layoutEmpty.root.gone()
                    }
                }
                adapter.submitList(it)
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
        customAlertDialogView.rootView.textInput.counterMaxLength = 35
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
        navigate(ListWorkFragmentDirections.actionSecondFragmentToDateTimePickerDialog(System.currentTimeMillis() + (60 * 1000)))
        setFragmentResultListener(FRAGMENT_RESULT_TIMER) { _, bundle ->
            item.timer = bundle.getLong(TIME_PICKER_BUNDLE)
            viewModel.updateContentData(item, wId)
            homeSharedViewModel.setNotifyDataInsert(
                AlarmNotificationEntity(
                    item.timer, mGroupId, item.id, item.name, resources.getString(R.string.notify_title)
                )
            )
        }
    }

    private fun setFragmentResultListener() {
        setFragmentResultListener(FRAGMENT_SETTING_OPTION) { _, bundle ->
            viewModel.saveTopicGroup(bundle.getInt(WorksSettingFragment.DATA_OPTION_SELECTED))
        }
    }

    private fun showAds() {
        val shared = requireContext().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        var sum = shared.getInt(CacheImpl.KEY_SUM_DONE_TASK, 0)
        if (sum < 10) {
            sum += 1
            shared.edit().putInt(CacheImpl.KEY_SUM_DONE_TASK, sum).apply()
        } else {
            shared.edit().putInt(CacheImpl.KEY_SUM_DONE_TASK, 0).apply()
            homeSharedViewModel.showAdsMobile.postValue(true)
        }
    }

    private fun checkFirstTapTap(): Boolean {
        val shared = requireActivity().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        return shared.getBoolean(CacheImpl.KEY_FIRST_TAP_TAP, false)
    }

    private fun setupLayout() {
        setupUI()
        setupCalendar()
    }

    private fun setupCalendar() {
        val picker = view?.findViewById(R.id.datePicker) as HorizontalPicker
        picker
            .setDays(20)
            .setOffset(10)
            .setDateSelectedColor(Color.DKGRAY)
            .setDateSelectedTextColor(Color.WHITE)
            .setMonthAndYearTextColor(Color.DKGRAY)
            .setTodayButtonTextColor(getColor(requireContext(), R.color.colorPrimary))
            .setTodayDateTextColor(getColor(requireContext(), R.color.colorPrimary))
            .setTodayDateBackgroundColor(Color.WHITE)
            .setUnselectedDayTextColor(Color.DKGRAY)
            .setDayOfWeekTextColor(Color.DKGRAY)
            .setUnselectedDayTextColor(getColor(requireContext(), R.color.bg_gray))
            .showTodayButton(false)
            .init()
        picker.backgroundColor = Color.LTGRAY
        picker.setDate(DateTime().plusDays(4))
        picker.setListener {
            mGroupId = TimestampUtils.getLongTimeFromStr(it.millis)
        }
    }
}