package com.example.reminds.ui.fragment.upcoming

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_UPCOMING
import com.example.framework.local.cache.CacheImpl
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentUpcomingBinding
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.ui.adapter.ListContentCheckAdapter
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.ui.fragment.detail.ListWorkViewModel
import com.example.reminds.ui.fragment.setting.WorksSettingFragment
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_custom_alert_text_input.view.*
import java.util.*

@AndroidEntryPoint
class UpcomingFragment : BaseFragment<FragmentUpcomingBinding>() {
    override fun getViewBinding(): FragmentUpcomingBinding {
        return FragmentUpcomingBinding.inflate(layoutInflater)
    }

    private val viewModel: ListWorkViewModel by viewModels()
    private val viewModelUpcoming: UpcomingViewModel by viewModels()
    private val homeSharedViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ListWorkAdapter
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

    private var mGroupId: Long = 0

    companion object {
        const val FRAGMENT_RESULT_TIMER = "FRAGMENT_RESULT_TIMER"
        const val FRAGMENT_SETTING_OPTION = "FRAGMENT_SETTING_OPTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun calGroupId() {
        mGroupId = TimestampUtils.getLongTimeFromStr(Calendar.getInstance().timeInMillis)
    }

    private fun setupListener() {
        mBinding.extendedFab.setOnClickListenerBlock {
            navigate(UpcomingFragmentDirections.actionUpcomingFragmentToOptionForWorkBSFragment(-1, TYPE_UPCOMING, mGroupId))
        }

        mBinding.rootWork.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {

            }

            override fun onDoubleClick(v: View?) {
                if (homeSharedViewModel.isKeyboardShow.value == true) {
                    viewModel.reSaveListWorkAndCreateStateFocus()
                    hideSoftKeyboard()
                } else {
                    viewModel.listWorkViewModel.lastOrNull()?.id?.let {
                        navigate(UpcomingFragmentDirections.actionUpcomingFragmentToOptionForWorkBSFragment(-1, TYPE_UPCOMING, mGroupId))
                    }
                }
            }

        })
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
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
                navigate(UpcomingFragmentDirections.actionUpcomingFragmentToOptionForWorkBSFragment(it.id, TYPE_UPCOMING, mGroupId))
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
        with(homeSharedViewModel) {
            isKeyboardShow.observe(viewLifecycleOwner, {
                (requireActivity() as? MainActivity)?.hideOrShowBottomAppBar(!it)
            })
        }

        with(viewModel) {
            listWorkData.observe(viewLifecycleOwner, { it ->
                when {
                    it.isEmpty() -> {
                        mBinding.layoutEmpty.root.visible()
                        mBinding.layoutEmpty.tvEmpty.text = resources.getString(R.string.empty_list)
                    }
                    it.sumByDouble { it.listContent.size.toDouble() }.toInt() == 0 && !checkFirstTapTap() -> {
                        mBinding.layoutEmpty.root.visible()
                        mBinding.layoutEmpty.tvEmpty.text = resources.getString(R.string.tap_tap)
                        mBinding.layoutEmpty.imgIcon.gone()
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

    private fun setupTimePickerForContent(item: ContentDataEntity, wId: Long) {
        navigate(UpcomingFragmentDirections.actionUpcomingFragmentToDateTimePickerDialog(System.currentTimeMillis() + (60 * 1000)))
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
  /*      val picker = view?.findViewById(R.id.datePicker) as HorizontalPicker
        picker
            .setDays(30)
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
        picker.backgroundColor = Color.WHITE
        picker.setDate(DateTime().plusDays(0))
        picker.setListener {
            mGroupId = TimestampUtils.getLongTimeFromStr(it.millis)
            viewModel.getListWork(mGroupId)
        }*/
    }
}