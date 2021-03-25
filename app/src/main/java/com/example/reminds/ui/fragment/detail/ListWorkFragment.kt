package com.example.reminds.ui.fragment.detail

import DateTimePickerFragment.Companion.TIME_PICKER_BUNDLE
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.common.base.model.AlarmNotificationEntity
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.TopicGroupEntity.Companion.TYPE_NORMAL
import com.example.framework.local.cache.CacheImpl
import com.example.framework.local.cache.CacheImpl.Companion.KEY_SUM_DONE_TASK
import com.example.reminds.R
import com.example.reminds.common.CallbackItemTouch
import com.example.reminds.common.MyItemTouchHelperCallback
import com.example.reminds.common.OnSwipeTouchListener
import com.example.reminds.ui.adapter.ListContentCheckAdapter
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.ui.fragment.setting.WorksSettingFragment.Companion.DATA_OPTION_SELECTED
import com.example.reminds.ui.sharedviewmodel.MainActivityViewModel
import com.example.reminds.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list_work.*
import kotlinx.android.synthetic.main.layout_empty.view.*
import kotlinx.android.synthetic.main.layout_empty_animation.view.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class ListWorkFragment : Fragment(), CallbackItemTouch {
    private val args by navArgs<ListWorkFragmentArgs>()

    private val viewModel: ListWorkViewModel by viewModels()
    private val homeSharedViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var adapter: ListWorkAdapter
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getListWork(args.idGroup)
        setFragmentResultListener()
        return inflater.inflate(R.layout.fragment_list_work, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUI()
        observeData()
        setupListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (args.idGroup != 1L) {
            inflater.inflate(R.menu.top_app_bar, menu)
        } else {
            inflater.inflate(R.menu.menu_main, menu)
        }
    }

    private fun setupListener() {
        extendedFab.setOnClickListenerBlock {
            navigate(ListWorkFragmentDirections.actionSecondFragmentToOptionForWorkBSFragment(-1, TYPE_NORMAL, args.idGroup))
        }
        rootWork.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeTop() {
                super.onSwipeTop()
                if (homeSharedViewModel.isKeyboardShow.value == true) {
                    viewModel.reSaveListWorkAndCreateStateFocus()
                    hideSoftKeyboard()
                } else {
                    viewModel.listWorkViewModel.lastOrNull()?.id?.let {
                        navigate(ListWorkFragmentDirections.actionSecondFragmentToOptionForWorkBSFragment(-1, TYPE_NORMAL, args.idGroup))
                    }
                }
            }
        })
    /*    rootWork.setOnClickListenerBlock {
            if (homeSharedViewModel.isKeyboardShow.value == true) {
                viewModel.reSaveListWorkAndCreateStateFocus()
                hideSoftKeyboard()
            } else {
                viewModel.listWorkViewModel.lastOrNull()?.id?.let {
                    navigate(ListWorkFragmentDirections.actionSecondFragmentToOptionForWorkBSFragment(-1, TYPE_NORMAL, args.idGroup))
                }
            }
        }
*/
/*        rootWork.setOnClickListenerBlock {
            if (homeSharedViewModel.isKeyboardShow.value == true) {
                viewModel.reSaveListWorkAndCreateStateFocus()
                hideSoftKeyboard()
            } else {
                viewModel.listWorkViewModel.lastOrNull()?.id?.let {
                    navigate(ListWorkFragmentDirections.actionSecondFragmentToOptionForWorkBSFragment(-1, TYPE_NORMAL, args.idGroup))
                }
            }
        }*/
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        activity?.actionBar?.title = args.titleGroup
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                navigate(ListWorkFragmentDirections.actionSecondFragmentToSettingFragment(args.idGroup))
            }
            android.R.id.home -> {
                navigateUp()
                return true
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
                navigate(ListWorkFragmentDirections.actionSecondFragmentToOptionForWorkBSFragment(it.id, TYPE_NORMAL, args.idGroup))
            }).apply {
            recyclerWorks.adapter = this
            val callback: ItemTouchHelper.Callback = MyItemTouchHelperCallback(this@ListWorkFragment)

            val touchHelper = ItemTouchHelper(callback)

            touchHelper.attachToRecyclerView(recyclerWorks)

        }
        homeSharedViewModel.isKeyboardShow.observe(viewLifecycleOwner, {
            if (!it) {
                viewModel.reSaveListWorkAndCreateStateFocus()
            }
        })
    }


    private fun observeData() {
        with(viewModel) {
            listWorkViewItems.observe(viewLifecycleOwner, { it ->
                when {
                    it.isEmpty() -> {
                        layoutEmpty.visible()
                        layoutEmpty.tvEmpty.text = resources.getString(R.string.empty_list)
                    }
                    /*    it.sumByDouble { it.listContent.size.toDouble() }.toInt() == 0 && !checkFirstTapTap() -> {
                            layoutEmpty.rootView.visible()
    //                        val shared = requireActivity().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
    //                        shared.edit().putBoolean(CacheImpl.KEY_FIRST_TAP_TAP, true).apply()
                        }*/
                    else -> {
                        layoutEmpty.gone()
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
        navigate(ListWorkFragmentDirections.actionSecondFragmentToDateTimePickerDialog(System.currentTimeMillis() + (60 * 1000)))
        setFragmentResultListener(FRAGMENT_RESULT_TIMER) { _, bundle ->
            item.timer = bundle.getLong(TIME_PICKER_BUNDLE)
            viewModel.updateContentData(item, wId)
            homeSharedViewModel.setNotifyDataInsert(
                AlarmNotificationEntity(
                    item.timer, args.idGroup, item.id, item.name, resources.getString(R.string.notify_title)
                )
            )
        }
    }

    private fun setFragmentResultListener() {
        setFragmentResultListener(FRAGMENT_SETTING_OPTION) { _, bundle ->
            viewModel.saveTopicGroup(bundle.getInt(DATA_OPTION_SELECTED))
        }
    }

    private fun showAds() {
        val shared = requireContext().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        var sum = shared.getInt(KEY_SUM_DONE_TASK, 0)
        if (sum < 10) {
            sum += 1
            shared.edit().putInt(KEY_SUM_DONE_TASK, sum).apply()
        } else {
            shared.edit().putInt(KEY_SUM_DONE_TASK, 0).apply()
            homeSharedViewModel.showAdsMobile.postValue(true)
        }
    }

    private fun checkFirstTapTap(): Boolean {
        val shared = requireActivity().getSharedPreferences(CacheImpl.SHARED_NAME, Context.MODE_PRIVATE)
        return shared.getBoolean(CacheImpl.KEY_FIRST_TAP_TAP, false)
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
