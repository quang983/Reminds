package com.example.reminds.ui.fragment.tabdaily.add

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import com.example.common.base.model.daily.DailyTaskEntity
import com.example.reminds.R
import com.example.reminds.common.BaseFragment
import com.example.reminds.common.RetrieveDataState
import com.example.reminds.databinding.FragmentAddDailyBinding
import com.example.reminds.service.ScheduledWorker
import com.example.reminds.service.everyday.NotificationDailyBroadcastReceiver
import com.example.reminds.ui.activity.MainActivity
import com.example.reminds.utils.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.*

@AndroidEntryPoint
class AddDailyTaskFragment : BaseFragment<FragmentAddDailyBinding>() {
    private val viewModel: AddDailyTaskViewModel by viewModels()

    override fun getViewBinding(): FragmentAddDailyBinding {
        return FragmentAddDailyBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupLayout()
        setOnClickListener()
        observerData()
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateUp()
                return true
            }
            R.id.action_save -> {
                viewModel.insertsDailyTask(
                    viewModel.taskInsertPreview.getOrDefault(
                        DailyTaskEntity(
                            System.currentTimeMillis(),
                            "", "", System.currentTimeMillis()
                        )
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupLayout() {
        (requireActivity() as? MainActivity)?.findViewById<AnimatedBottomBar>(R.id.bottom_navigation)?.gone()
    }

    private fun setOnClickListener() {
        mBinding.chipGroupFrequence.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_daily -> {
                    mBinding.groupPickDay.gone()

                }
                R.id.chip_weeks -> {
                    mBinding.groupPickDay.visible()
                }
            }
        }

        mBinding.edtInputName.setTextChangedListener {
            viewModel.taskInsertPreview.getOrNull()?.name = it.text.toString()
        }

        mBinding.edtInputDesc.setTextChangedListener {
            viewModel.taskInsertPreview.getOrNull()?.name = it.text.toString()
        }

        mBinding.tvTimeRemaining.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Select Appointment time")
                    .build()
            picker.show(childFragmentManager, null)
            picker.addOnPositiveButtonClickListener {
                viewModel.taskInsertPreview.getOrNull()?.remainingTime = (picker.hour + picker.minute).toLong()
                mBinding.tvTimeRemainingValue.text = "${picker.hour}:${picker.minute}"
            }
            picker.addOnNegativeButtonClickListener {
            }
        }

        mBinding.tvTimeEnd.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .build()
            datePicker.show(childFragmentManager, null)
            datePicker.addOnPositiveButtonClickListener {
                viewModel.taskInsertPreview.getOrNull()?.endTime = datePicker.selection
                mBinding.tvTimeEndValue.text = TimestampUtils.getDate(datePicker.selection ?: 0)
            }
            datePicker.addOnNegativeButtonClickListener {
            }
        }
    }

    private fun observerData() {
        viewModel.stateInsertData.observe(viewLifecycleOwner, {
            when (it) {
                is RetrieveDataState.Start -> {

                }

                is RetrieveDataState.Success -> {
                    alarm()
                    navigateUp()
                }

                is RetrieveDataState.Failure -> {

                }
            }
        })

        viewModel.taskInsertPreview.observe(viewLifecycleOwner, {
        })
    }

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent


    private fun alarm() {
        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, NotificationDailyBroadcastReceiver::class.java).let { intent ->
            intent.putExtra(ScheduledWorker.NOTIFICATION_TITLE, "Title")
            intent.putExtra(ScheduledWorker.NOTIFICATION_MESSAGE, "Message")
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 50)
        }

        alarmMgr?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            86400000,
            alarmIntent
        )
    }
}