package com.example.reminds.ui.fragment.tabdaily.detail

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.databinding.CalendarDayBinding
import com.example.reminds.databinding.FragmentDailyTaskDetailBinding
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.utils.getColorCompat
import com.example.reminds.utils.gone
import com.example.reminds.utils.postValue
import com.example.reminds.utils.visibleOrGone
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.ncorti.slidetoact.SlideToActView
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class DailyTaskDetailFragment : BaseFragment<FragmentDailyTaskDetailBinding>() {
    private val args by navArgs<DailyTaskDetailFragmentArgs>()

    @Inject
    lateinit var assistedFactory: DailyDetailViewModelAssistedFactory

    private val _viewModel: DailyTaskDetailViewModel by viewModels {
        DailyTaskDetailViewModel.Factory(assistedFactory, args.id)
    }

    private var selectedDate = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")
    private val dayFormatter = DateTimeFormatter.ofPattern("EEE")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMM")

    override fun getViewBinding(): FragmentDailyTaskDetailBinding {
        return FragmentDailyTaskDetailBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout()
        setupCalendarView()
        observer()
        setupClickListener()
    }

    private fun setupClickListener() {
    }


    private fun setupLayout() {
        (requireActivity() as? MainActivity)?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.gone()
        mBinding.slideToUnlock.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                if (view.isCompleted()) {
                    _viewModel.updateDividerInDailyTask()
                }
            }
        }
    }

    private fun observer() {
        _viewModel.getDetailDailyTask.observe(viewLifecycleOwner, {

        })

        _viewModel.showCheckInLiveData.observe(viewLifecycleOwner, {
            mBinding.slideToUnlock.visibleOrGone(it)
            mBinding.slideToUnlock.resetSlider()
            mBinding.layoutCheckDone.root.visibleOrGone(!it)
        })
    }

    private fun setupCalendarView() {
        val dm = DisplayMetrics()
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.display
        } else {
            activity?.windowManager?.defaultDisplay
        }
        display?.getRealMetrics(dm)
        mBinding.calendarView.apply {
            val dayWidth = dm.widthPixels / 5
            val dayHeight = (dayWidth * 1.25).toInt()
            daySize = com.kizitonwose.calendarview.utils.Size(dayWidth, dayHeight)
        }

        mBinding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) = container
                .bind(day, _viewModel.getDetailDailyTask.value?.dailyTask?.createTime ?: 0L)
        }
        val currentMonth = YearMonth.now()
        val firstDayOfWeeks = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        mBinding.calendarView.setup(currentMonth, currentMonth.plusMonths(3), firstDayOfWeeks)
        mBinding.calendarView.scrollToDate(LocalDate.now())
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val binding = CalendarDayBinding.bind(view)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                val cal = Calendar.getInstance()
                cal.timeInMillis = _viewModel.getDetailDailyTask.value?.dailyTask?.createTime ?: 0L
                if (!(day.date.year < cal[Calendar.YEAR] || day.date.year >= cal[Calendar.YEAR] &&
                            day.date.dayOfYear < cal[Calendar.DAY_OF_YEAR])
                ) {
                    mBinding.calendarView.smoothScrollToDate(day.date)
                    val firstDay = mBinding.calendarView.findFirstVisibleDay()
                    val lastDay = mBinding.calendarView.findLastVisibleDay()
                    if (firstDay == day) {
                        mBinding.calendarView.smoothScrollToDate(day.date)
                    } else if (lastDay == day) {
                        mBinding.calendarView.smoothScrollToDate(day.date.minusDays(4))
                    }

                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        mBinding.calendarView.notifyDateChanged(day.date)
                        oldDate?.let {
                            mBinding.calendarView.notifyDateChanged(it)
                        }
                    }
                    _viewModel.localDateChecked.postValue(day.date)
                }
            }
        }

        fun bind(day: CalendarDay, startDateMillis: Long) {
            this.day = day
            binding.tvDate.text = dateFormatter.format(day.date)
            binding.tvDay.text = dayFormatter.format(day.date)
            binding.tvMonth.text = monthFormatter.format(day.date)
            binding.tvDate.setTextColor(view.context.getColorCompat(if (day.date == selectedDate) R.color.red else R.color.black))
            binding.tvDay.setTextColor(view.context.getColorCompat(if (day.date == selectedDate) R.color.red else R.color.black))
            val cal = Calendar.getInstance()
            cal.timeInMillis = startDateMillis
            if (day.date.year < cal[Calendar.YEAR] || day.date.year >= cal[Calendar.YEAR] &&
                day.date.dayOfYear < cal[Calendar.DAY_OF_YEAR]
            ) {
                binding.tvDate.alpha = 0.3f
                binding.tvDay.alpha = 0.3f
            } else {
                binding.tvDate.alpha = 1f
                binding.tvDay.alpha = 1f
            }
        }
    }
}
