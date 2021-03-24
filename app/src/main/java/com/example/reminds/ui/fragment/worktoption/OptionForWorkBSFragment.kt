package com.example.reminds.ui.fragment.worktoption

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.base.model.AlarmNotificationEntity
import com.example.reminds.R
import com.example.reminds.ui.fragment.detail.ListWorkFragment
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_option_for_work_bs.*

@AndroidEntryPoint
class OptionForWorkBSFragment : BottomSheetDialogFragment() {
    private val args by navArgs<OptionForWorkBSFragmentArgs>()

    private val viewModel: OptionForWorkBSViewModel by viewModels()

    private val homeSharedViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_option_for_work_bs, container, false)
        getData()
        setupFragmentResultListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUI()
        setupObserver()
        setListener()
    }

    private fun getData() {
        when (args.idWork) {
            -1L -> {
                viewModel.idGroup.postValue(args.idTopic)
            }
            else -> {
                viewModel.setIdWork(args.idWork)
            }
        }
    }

    private fun setupUI() {
        edtInput.requestFocus()
    }

    private fun setupObserver() {
        viewModel.idGroup.observe(viewLifecycleOwner, {
        })

        viewModel.progressUpdateWork.observe(viewLifecycleOwner, { it ->
            if (it) {
                viewModel.workDataPrepareLiveData.value?.takeIf { it.timerReminder > 0 }?.let {
                    homeSharedViewModel.setNotifyDataInsert(
                        AlarmNotificationEntity(
                            it.timerReminder, it.groupId, it.id, it.name, resources.getString(R.string.home_screen_title)
                        )
                    )
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.warning_title_min), Toast.LENGTH_SHORT).show()
            }
            Handler().postDelayed({ dismiss() }, 200)
        })

        viewModel.workDataPrepareLiveData.observe(viewLifecycleOwner, {

            edtInput.checkDiffAndSetText(it.name)

            tvClockInfo.visibleOrGone(it.timerReminder > 0)

            if (it.timerReminder > 0) {
                tvClockInfo.text = TimestampUtils.getFullFormatTime(it.timerReminder, TimestampUtils.INCREASE_DATE_FORMAT)
            }
        })
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
    }

    private fun setListener() {
        btnDelete.setOnClickListenerBlock {
            showAlertDeleteDialog(resources.getString(R.string.content_delete_topic_title)) {
                viewModel.deleteWork()
                dismiss()
            }
        }

        btnBack.setOnClickListenerBlock {
            dismiss()
        }

        btnSave.setOnClickListenerBlock {
            if (edtInput.text.toString().isNotBlank()) {
                viewModel.saveWorkIntoDataBase(args.typeTopic)
            } else {
                Toast.makeText(requireContext(), getString(R.string.warning_title_min), Toast.LENGTH_SHORT).show()
            }
        }

        tvClockInfo.setOnClickListenerBlock {
            setupTimePickerForContent()
        }

        imgSettingClock.setOnClickListenerBlock {
            setupTimePickerForContent()
        }

        edtInput.setTextChangedListener {
            viewModel.setNameWorkDataPrepare(it.text.toString())
            if (it.text.isNotBlank()) {
                btnSave.setTextColor(requireContext().resources.getColor(R.color.blue_900))
            } else {
                btnSave.setTextColor(requireContext().resources.getColor(R.color.bg_gray))
            }
        }
    }


    private fun setupFragmentResultListener() {
        setFragmentResultListener(ListWorkFragment.FRAGMENT_RESULT_TIMER) { _, bundle ->
            val timer = bundle.getLong(TIME_PICKER_BUNDLE)
            viewModel.setTimerWorkDataPrepare(timer)
        }
    }

    private fun setupTimePickerForContent() {
        navigate(OptionForWorkBSFragmentDirections.actionOptionForWorkBSFragmentToDateTimePickerDialog(System.currentTimeMillis() + (60 * 1000)))
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

}