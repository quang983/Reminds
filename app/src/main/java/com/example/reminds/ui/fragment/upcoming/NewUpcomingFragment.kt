package com.example.reminds.ui.fragment.upcoming

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_UPCOMING
import com.example.framework.local.cache.CacheImpl
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.common.CallbackItemTouch
import com.example.reminds.common.MyItemTouchHelperCallback
import com.example.reminds.databinding.FragmentUpcomingNewBinding
import com.example.reminds.databinding.LayoutCalendarDayBinding
import com.example.reminds.databinding.LayoutCalendarHeaderBinding
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.ui.adapter.ListWorkUpcomingAdapter
import com.example.reminds.ui.fragment.detail.ListWorkViewModel
import com.example.reminds.ui.fragment.setting.WorksSettingFragment
import com.example.reminds.ui.sharedviewmodel.FocusActivityViewModel
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.example.reminds.utils.DateUtils.toMilisTime
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_upcoming_new.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class NewUpcomingFragment : BaseFragment<FragmentUpcomingNewBinding>(), CallbackItemTouch {
    private val viewModelUpcoming: UpcomingViewModel by viewModels()
    private val viewModelFocusShared: FocusActivityViewModel by activityViewModels()
    private val viewModel: ListWorkViewModel by viewModels()
    private val homeSharedViewModel: MainActivityViewModel by activityViewModels()

    override fun getViewBinding(): FragmentUpcomingNewBinding {
        return FragmentUpcomingNewBinding.inflate(layoutInflater)
    }

    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = hashMapOf<Long, String>()

    private lateinit var adapter: ListWorkUpcomingAdapter
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

    private lateinit var dayOfWeek: Array<String>

    private var mGroupId: Long = 0


    companion object {
        const val FRAGMENT_RESULT_TIMER = "FRAGMENT_RESULT_TIMER"
        const val FRAGMENT_SETTING_OPTION = "FRAGMENT_SETTING_OPTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dayOfWeek = resources.getStringArray(R.array.day)
        getListWork(homeSharedViewModel.selectedDate)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupCalendar()
        setupLayout()
        observeData()
        setupListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_upcoming, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_expanded -> {
                setCalendarChangeSize(viewModelUpcoming.isExpandedCalendar)
                viewModelUpcoming.isExpandedCalendar = !viewModelUpcoming.isExpandedCalendar
            }
            R.id.action_focus_today -> {
                selectDate(homeSharedViewModel.today)
                mBinding.datePicker.scrollToDate(homeSharedViewModel.selectedDate)
            }
            android.R.id.home -> {
            }
        }
        return super.onOptionsItemSelected(item)

    }

    private fun selectDate(date: LocalDate) {
        if (homeSharedViewModel.selectedDate != date) {
            val oldDate = homeSharedViewModel.selectedDate
            homeSharedViewModel.selectedDate = date
            oldDate.let { mBinding.datePicker.notifyDateChanged(it) }
            mBinding.datePicker.notifyDateChanged(date)
            updateAdapterForDate(date)
            getListWork(date)
        }
    }

    private fun getListWork(date: LocalDate) {
        mGroupId = TimestampUtils.getLongTimeFromStr(date.toString().toMilisTime())
        viewModel.getListWork(mGroupId)
    }

    private fun updateAdapterForDate(date: LocalDate) {
        mBinding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    private fun setupListener() {
        mBinding.extendedFab.setOnClickListenerBlock {
            navigate(NewUpcomingFragmentDirections.actionNewUpcomingFragmentToOptionForWorkBSFragment(-1, TYPE_UPCOMING, mGroupId))
        }
        mBinding.rootWork.setOnClickListenerBlock {
            if (homeSharedViewModel.isKeyboardShow.value == true) {
                viewModel.reSaveListWorkAndCreateStateFocus()
                hideSoftKeyboard()
            }
        }
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
    }

    private fun setupUI() {
        updateAdapterForDate(homeSharedViewModel.selectedDate)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        adapter = ListWorkUpcomingAdapter(handlerCheckedAll = { workId, doneAll ->
            viewModel.handleDoneAllContentFromWork(workId, doneAll)
        }, {
            navigate(NewUpcomingFragmentDirections.actionNewUpcomingFragmentToOptionForWorkBSFragment(it.id, TYPE_UPCOMING, it.groupId))
        }, {
            viewModelFocusShared.itemWorkSelected.postValue(it)
            (requireActivity() as? MainActivity)?.bottom_navigation?.selectTabAt(2, true)
        }).apply {
            mBinding.recyclerWorks.adapter = this
            val callback: ItemTouchHelper.Callback = MyItemTouchHelperCallback(this@NewUpcomingFragment)

            val touchHelper = ItemTouchHelper(callback)

            touchHelper.attachToRecyclerView(recyclerWorks)
        }
    }


    private fun observeData() {
        with(homeSharedViewModel) {
            isKeyboardShow.observe(viewLifecycleOwner, {
                (requireActivity() as? MainActivity)?.hideOrShowBottomAppBar(!it)
                if (!it) {
                    viewModel.reSaveListWorkAndCreateStateFocus()
                }
            })
        }

        with(viewModel) {
            listWorkViewItems.observe(viewLifecycleOwner, {
                when {
                    it.isEmpty() -> {
                        mBinding.layoutEmpty.root.visible()
                        mBinding.layoutEmpty.tvEmpty.text = resources.getString(R.string.empty_list)
                    }
                    else -> {
                        mBinding.layoutEmpty.root.gone()
                    }
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

    private fun setupLayout() {
        setupUI()
    }

    private fun setupCalendar() {
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        mBinding.datePicker.apply {
            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
            mBinding.datePicker.scrollToDate(homeSharedViewModel.selectedDate)
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
                        homeSharedViewModel.today -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.bg_today)
                            dotView.invisible()
                        }
                        homeSharedViewModel.selectedDate -> {
                            textView.setTextColorRes(R.color.white)
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
                        tv.text = dayOfWeek[index]
                        tv.setTextColorRes(R.color.black)
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setCalendarChangeSize(monthToWeek: Boolean) {
        (requireActivity() as? MainActivity)?.toolbar?.menu?.findItem(R.id.action_expanded)?.icon = if (monthToWeek)
            resources.getDrawable(R.drawable.ic_expand, null) else resources.getDrawable(R.drawable.ic_shrink, null)
        val firstDate = homeSharedViewModel.selectedDate
        val lastDate = mBinding.datePicker.findLastVisibleDay()?.date ?: return

        val oneWeekHeight = mBinding.datePicker.daySize.height * 2
        val oneMonthHeight = mBinding.datePicker.daySize.height * 6

        val oldHeight = if (monthToWeek) oneMonthHeight else oneWeekHeight
        val newHeight = if (monthToWeek) oneWeekHeight else oneMonthHeight

        val animator = ValueAnimator.ofInt(oldHeight, newHeight)
        animator.addUpdateListener { animator ->
            mBinding.datePicker.updateLayoutParams {
                height = animator.animatedValue as Int
            }
        }

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
                mBinding.datePicker.scrollToDate(firstDate)
            } else {
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    mBinding.datePicker.scrollToMonth(firstDate.yearMonth)
                } else {
                    val currentMonth = YearMonth.now()
                    val endMonth = currentMonth.plusMonths(10)
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
        lateinit var day: CalendarDay
        val binding = LayoutCalendarDayBinding.bind(view)

        init {
            view.setOnClickListener {
                if (day.owner == DayOwner.THIS_MONTH) {
                    selectDate(day.date)
                }
            }
        }
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int) {
        val list = adapter.currentList.toMutableList().apply {
            val item = removeAt(oldPosition)
            add(newPosition, item)
        }
        adapter.submitList(list)
    }

    override fun itemTouchOnMoveFinish() {
        viewModel.saveListWork(adapter.currentList)
    }
}