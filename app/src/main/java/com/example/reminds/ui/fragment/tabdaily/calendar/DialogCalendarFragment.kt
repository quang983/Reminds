package com.example.reminds.ui.fragment.tabdaily.calendar

import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.reminds.R
import com.example.reminds.common.BaseDialogFragment
import com.example.reminds.databinding.CalendarMonthHeaderBinding
import com.example.reminds.databinding.FragmentCalendarDialogBinding
import com.example.reminds.databinding.ItemDayCalendarBinding
import com.example.reminds.ui.fragment.tabdaily.detail.DailyDetailViewModelAssistedFactory
import com.example.reminds.ui.fragment.tabdaily.detail.DailyTaskDetailViewModel
import com.example.reminds.utils.daysOfWeekFromLocale
import com.example.reminds.utils.invisible
import com.example.reminds.utils.setTextColorRes
import com.example.reminds.utils.visible
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DialogCalendarFragment : BaseDialogFragment<FragmentCalendarDialogBinding>() {
    private var selectedDate = LocalDate.now()
    private val today = LocalDate.now()

    private val args by navArgs<DialogCalendarFragmentArgs>()

    @Inject
    lateinit var assistedFactory: DailyDetailViewModelAssistedFactory

    private val _viewModel: DailyTaskDetailViewModel by navGraphViewModels(R.id.navigation_daily) {
        DailyTaskDetailViewModel.Factory(assistedFactory, args.id)
    }

    override fun getViewBinding(): FragmentCalendarDialogBinding {
        return FragmentCalendarDialogBinding.inflate(layoutInflater)
    }

    override fun setupLayout() {
        setupCalendarView()
    }

    private fun setupCalendarView() {
        val daysOfWeek = daysOfWeekFromLocale()
        mBinding.layoutHeader.root.children.forEachIndexed { index, view ->
            (view as? TextView)?.apply {
                text = daysOfWeek[index].name.first().toString()
                setTextColorRes(R.color.white)
            }
        }
        mBinding.exTwoCalendar.setup(YearMonth.now(), YearMonth.now().plusMonths(10), daysOfWeek.first())

        mBinding.exTwoCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val binding = ItemDayCalendarBinding.bind(view)

            init {
                binding.root.setOnClickListener {
                    if (!_viewModel.checkBeforeTime(day, _viewModel.getDetailDailyTask.value?.dailyTask?.createTime ?: 0L)
                    ) {
                        if (day.owner == DayOwner.THIS_MONTH) {
                            if (selectedDate == day.date) {
                                selectedDate = null
                                mBinding.exTwoCalendar.notifyDayChanged(day)
                            } else {
                                val oldDate = selectedDate
                                selectedDate = day.date
                                mBinding.exTwoCalendar.notifyDateChanged(day.date)
                                oldDate?.let { mBinding.exTwoCalendar.notifyDateChanged(oldDate) }
                            }
                        }
                    }
                }
            }
        }


        mBinding.exTwoCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            val listChecked = _viewModel.listDoneTime.value
            val createTime = _viewModel.getDetailDailyTask.value?.dailyTask?.createTime ?: 0L

            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.binding.exTwoDayText.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    container.binding.exTwoDayText.visible()
                    when (day.date) {
                        selectedDate -> {
                            container.binding.exTwoDayText.setTextColorRes(R.color.black)
                            container.binding.exTwoDayText.setBackgroundResource(R.drawable.selected_dot)
                        }
                        today -> {
                            container.binding.exTwoDayText.setTextColorRes(R.color.red)
                            container.binding.exTwoDayText.background = null
                        }
                        else -> {
                            container.binding.exTwoDayText.setTextColorRes(R.color.black)
                            container.binding.exTwoDayText.background = null
                        }
                    }
                } else {
                    container.binding.exTwoDayText.invisible()
                }
                if (listChecked?.takeIf { it.isNotEmpty() }?.any {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it
                        cal[Calendar.YEAR] == day.date.year &&
                                cal[Calendar.DAY_OF_YEAR] == day.date.dayOfYear
                    } == true) {
                    container.binding.exTwoDayText.setBackgroundResource(R.drawable.ic_check_done)
                } else {
                    container.binding.exTwoDayText.background = null
                }
                if (_viewModel.checkBeforeTime(day, createTime)
                ) {
                    container.binding.exTwoDayText.alpha = 0.3f
                } else {
                    container.binding.exTwoDayText.alpha = 1f
                }
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarMonthHeaderBinding.bind(view).exTwoHeaderText
    }

    override fun setupObserver() {
        _viewModel.listDoneTime.observe(viewLifecycleOwner, {
            mBinding.exTwoCalendar.notifyCalendarChanged()
        })
    }
}