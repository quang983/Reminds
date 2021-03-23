package com.example.reminds.ui.fragment.upcoming

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_UPCOMING
import com.example.framework.local.cache.CacheImpl
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.FragmentUpcomingNewBinding
import com.example.reminds.databinding.LayoutCalendarDayBinding
import com.example.reminds.databinding.LayoutCalendarHeaderBinding
import com.example.reminds.databinding.LayoutUpcomingBinding
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.ui.adapter.ListContentCheckAdapter
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.ui.fragment.detail.ListWorkViewModel
import com.example.reminds.ui.fragment.setting.WorksSettingFragment
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.example.reminds.utils.DateUtils.toMilisTime
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.yearMonth
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs


@AndroidEntryPoint
class NewUpcomingFragment : BaseFragment<FragmentUpcomingNewBinding>() {
    override fun getViewBinding(): FragmentUpcomingNewBinding {
        return FragmentUpcomingNewBinding.inflate(layoutInflater)
    }

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = hashMapOf<Long, String>()

    private val viewModelUpcoming: UpcomingViewModel by viewModels()
    private val viewModel: ListWorkViewModel by viewModels()
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
        setupCalendar(savedInstanceState)
        setupLayout()
        observeData()
        setupListener()
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { mBinding.datePicker.notifyDateChanged(it) }
            mBinding.datePicker.notifyDateChanged(date)
            updateAdapterForDate(date)
            getListWork(date)
        }
    }

    private fun getListWork(date: LocalDate) {
        mGroupId = TimestampUtils.getLongTimeFromStr(date.toString().toMilisTime())
        viewModel.getListWork(mGroupId)
    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), R.string.example_3_empty_input_text, Toast.LENGTH_LONG).show()
        } else {
            selectedDate?.let {
//                events[it] = events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
            }
        }
    }

/*    private fun deleteEvent(event: Event) {
        val date = event.date
//        events[date] = events[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }*/

    private fun updateAdapterForDate(date: LocalDate) {
        mBinding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }


    private fun calGroupId() {
        mGroupId = TimestampUtils.getLongTimeFromStr(Calendar.getInstance().timeInMillis)
    }

    private fun setupListener() {
        mBinding.extendedFab.setOnClickListenerBlock {
            navigate(NewUpcomingFragmentDirections.actionNewUpcomingFragmentToOptionForWorkBSFragment(-1, TYPE_UPCOMING, mGroupId))
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
                        navigate(NewUpcomingFragmentDirections.actionNewUpcomingFragmentToOptionForWorkBSFragment(-1, TYPE_UPCOMING, mGroupId))
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
                navigate(NewUpcomingFragmentDirections.actionNewUpcomingFragmentToOptionForWorkBSFragment(it.id, TYPE_UPCOMING, mGroupId))
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
                    /*         it.isEmpty() -> {
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
                    }*/
                }
                adapter.submitList(it)
            })
        }

        with(viewModelUpcoming) {
            getAllTopicUpComing.observe(viewLifecycleOwner, { it ->
                it.forEach {
                    events[it.id] = it.name
                }
                mBinding.datePicker.notifyCalendarChanged()
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
        navigate(NewUpcomingFragmentDirections.actionNewUpcomingFragmentToDateTimePickerDialog(System.currentTimeMillis() + (60 * 1000)))
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
    }

    private fun setupCalendar(savedInstanceState: Bundle?) {
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        mBinding.datePicker.apply {
            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            mBinding.datePicker.post {
                selectDate(today)
            }
        }

        mBinding.datePicker.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.visible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.bg_today)
                            dotView.invisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.example_3_blue)
                            textView.setBackgroundResource(R.drawable.bg_selected_date)
                            dotView.invisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                            dotView.visibleOrInvisible(events.containsKey(day.date.toString().toMilisTime()))
                        }
                    }
                } else {
                    textView.invisible()
                    dotView.invisible()
                }
            }
        }
/*
        binding.datePicker.monthScrollListener = {
            *//*     homeActivityToolbar.title = if (it.year == today.year) {
                     titleSameYearFormatter.format(it.yearMonth)
                 } else {
                     titleFormatter.format(it.yearMonth)
                 }*//*

            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(it.yearMonth.atDay(1))
        }*/

        mBinding.datePicker.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.first().toString()
                        tv.setTextColorRes(R.color.black)
                    }
                }
            }
        }

/*        binding.weekModeCheckBox.setOnCheckedChangeListener { _, monthToWeek ->
            setCalendarChangeSize(monthToWeek)
        }*/
    }

    private fun setCalendarChangeSize(monthToWeek: Boolean) {
        val firstDate = selectedDate ?: LocalDate.now()
        val lastDate = mBinding.datePicker.findLastVisibleDay()?.date ?: return

        val oneWeekHeight = mBinding.datePicker.daySize.height * 2
        val oneMonthHeight = mBinding.datePicker.daySize.height * 6

        val oldHeight = if (monthToWeek) oneMonthHeight else oneWeekHeight
        val newHeight = if (monthToWeek) oneWeekHeight else oneMonthHeight

        // Animate calendar height changes.
        val animator = ValueAnimator.ofInt(oldHeight, newHeight)
        animator.addUpdateListener { animator ->
            mBinding.datePicker.updateLayoutParams {
                height = animator.animatedValue as Int
            }
        }

        // When changing from month to week mode, we change the calendar's
        // config at the end of the animation(doOnEnd) but when changing
        // from week to month mode, we change the calendar's config at
        // the start of the animation(doOnStart). This is so that the change
        // in height is visible. You can do this whichever way you prefer.

        animator.doOnStart {
            if (!monthToWeek) {
                mBinding.datePicker.updateMonthConfiguration(
                    inDateStyle = InDateStyle.ALL_MONTHS,
                    maxRowCount = 6,
                    hasBoundaries = true
                )
            }
        }
        animator.doOnEnd {
            if (monthToWeek) {
                mBinding.datePicker.updateMonthConfiguration(
                    inDateStyle = InDateStyle.FIRST_MONTH,
                    maxRowCount = 1,
                    hasBoundaries = false
                )
            }

            if (monthToWeek) {
                // We want the first visible day to remain
                // visible when we change to week mode.
                mBinding.datePicker.scrollToDate(firstDate)
            } else {
                // When changing to month mode, we choose current
                // month if it is the only one in the current frame.
                // if we have multiple months in one frame, we prefer
                // the second one unless it's an outDate in the last index.
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    mBinding.datePicker.scrollToMonth(firstDate.yearMonth)
                } else {
                    val currentMonth = YearMonth.now()
                    val endMonth = currentMonth.plusMonths(10)
                    // We compare the next with the last month on the calendar so we don't go over.
                    mBinding.datePicker.scrollToMonth(minOf(firstDate.yearMonth.next, endMonth))
                }
            }
        }
        animator.duration = 250
        animator.start()
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val legendLayout = LayoutCalendarHeaderBinding.bind(view).legendLayout.root
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay // Will be set when this container is bound.
        val binding = LayoutCalendarDayBinding.bind(view)

        init {
            view.setOnClickListener {
                if (day.owner == DayOwner.THIS_MONTH) {
                    selectDate(day.date)
                }
            }
        }
    }
}