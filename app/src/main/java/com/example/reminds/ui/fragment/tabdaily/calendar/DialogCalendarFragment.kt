package com.example.reminds.ui.fragment.tabdaily.calendar

import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.reminds.R
import com.example.reminds.common.BaseDialogFragment
import com.example.reminds.databinding.CalendarMonthHeaderBinding
import com.example.reminds.databinding.FragmentCalendarDialogBinding
import com.example.reminds.databinding.ItemDayCalendarBinding
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

@AndroidEntryPoint
class DialogCalendarFragment : BaseDialogFragment<FragmentCalendarDialogBinding>() {
    private var selectedDate = LocalDate.now()
    private val today = LocalDate.now()

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

        mBinding.exTwoCalendar.dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) = container.bind()
        }
    }


    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay
        val binding = ItemDayCalendarBinding.bind(view)

        init {
            binding.root.setOnClickListener {
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
//                        menuItem.isVisible = selectedDate != null
                }
            }
        }

        fun bind() {
            mBinding.exTwoCalendar.dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    binding.exTwoDayText.text = day.date.dayOfMonth.toString()
                    if (day.owner == DayOwner.THIS_MONTH) {
                        binding.exTwoDayText.visible()
                        when (day.date) {
                            selectedDate -> {
                                binding.exTwoDayText.setTextColorRes(R.color.black)
                                binding.exTwoDayText.setBackgroundResource(R.drawable.selected_dot)
                            }
                            today -> {
                                binding.exTwoDayText.setTextColorRes(R.color.red)
                                binding.exTwoDayText.background = null
                            }
                            else -> {
                                binding.exTwoDayText.setTextColorRes(R.color.black)
                                binding.exTwoDayText.background = null
                            }
                        }
                    } else {
                        binding.exTwoDayText.invisible()
                    }
                }
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = CalendarMonthHeaderBinding.bind(view).exTwoHeaderText
    }

    override fun setupObserver() {
    }
}