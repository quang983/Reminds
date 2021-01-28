import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.reminds.R
import com.example.reminds.ui.fragment.datetime.DateTimePickerViewModel
import com.example.reminds.utils.getTime
import com.example.reminds.utils.navigateUp
import com.example.reminds.utils.setTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.android.synthetic.main.fragment_date_time_picker.*
import kotlinx.android.synthetic.main.fragment_date_time_picker.view.*
import kotlinx.android.synthetic.main.layout_calendar.view.*
import kotlinx.android.synthetic.main.layout_time.view.*
import java.util.*

class DateTimePickerFragment : BottomSheetDialogFragment(), TimePicker.OnTimeChangedListener {
    private val viewModel: DateTimePickerViewModel by viewModels()

    private val args by navArgs<DateTimePickerFragmentArgs>()

    companion object {
        const val TIME_PICKER_BUNDLE = "TIME_PICKER_BUNDLE"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDataFromArgs()
        val view = inflater.inflate(R.layout.fragment_date_time_picker, container, false)
        setUpView(view)
        setUpDateTimePicker(view)
        return view
    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        viewModel.onTimeChanged(hourOfDay, minute)
    }

    private fun getDataFromArgs() {
        viewModel.setUpTime(args.time, args.minimum, args.maximum)
    }

    private fun setUpView(view: View) {
        val daysOfWeek: Array<CharSequence> = requireContext().resources.getTextArray(R.array.firstDayOfWeek)
        val monthsOfYear: Array<CharSequence> = requireContext().resources.getTextArray(R.array.MonthList)
        view.date.setWeekDayLabels(daysOfWeek)
        view.date.setTitleMonths(monthsOfYear)

        view.date.isPagingEnabled = false
        view.date.clipToPadding = false
        view.date.isDynamicHeightEnabled = false
        view.time.setIs24HourView(true)
        view.time.setOnTimeChangedListener(this)

        view.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewSwitchcer.showNext()
                    1 -> viewSwitchcer.showPrevious()
                }
            }
        })

        view.date.setOnDateChangedListener { _: MaterialCalendarView?, date: CalendarDay, _: Boolean ->
            viewModel.onDateChanged(date.year, date.month - 1, date.day)
            view.time.setTime(Pair(0, 0))
        }

        view.tvOk.setOnClickListener {
            val time = view.time.getTime()
            viewModel.onTimeChanged(time.first, time.second)
            viewModel.calendar.value?.let {
                setFragmentResult(TIME_PICKER_BUNDLE, bundleOf(TIME_PICKER_BUNDLE to it.timeInMillis / 1000))
            }
            navigateUp()
        }
        view.tvCancel.setOnClickListener {
            navigateUp()
        }
    }

    private fun setUpDateTimePicker(view: View) {
        viewModel.calendar.value?.let {
            val day = CalendarDay.from(it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH))
            view.date.setDateSelected(day, true)
            view.date.currentDate = day
            view.date.setDateSelected(day, true)
            view.time.setTime(Pair(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE)))
        }

        val edit: MaterialCalendarView.StateBuilder = view.date.state().edit()
        viewModel.maximum.value?.let {
            val dayMax = CalendarDay.from(it[Calendar.YEAR], it[Calendar.MONTH] + 1, it[Calendar.DAY_OF_MONTH])
            edit.setMaximumDate(dayMax)
        }
        viewModel.minimum.value?.let {
            val dayMin = CalendarDay.from(it[Calendar.YEAR], it[Calendar.MONTH] + 1, it[Calendar.DAY_OF_MONTH])
            edit.setMinimumDate(dayMin)
        }
        edit.isCacheCalendarPositionEnabled(true).commit()
    }
}