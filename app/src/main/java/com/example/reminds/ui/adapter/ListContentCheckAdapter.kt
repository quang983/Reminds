package com.example.reminds.ui.adapter

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.DiffUtil
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.common.base.model.ContentDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.*
import com.example.reminds.utils.TimestampUtils.INCREASE_DATE_FORMAT
import kotlinx.android.synthetic.main.item_content_check.view.*
import java.util.*
import kotlin.collections.ArrayList


class ListContentCheckAdapter(
    private val insertItemClick: (item: ContentDataEntity) -> Unit,
    private val handlerCheckItem: (item: ContentDataEntity) -> Unit,
    private val updateNameContent: (item: ContentDataEntity) -> Unit,
    private val moreActionClick: (item: ContentDataEntity, type: Int) -> Unit
) : BaseAdapter<ContentDataEntity>(

    object : DiffUtil.ItemCallback<ContentDataEntity>() {

        override fun areItemsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
            return oldItem.idOwnerWork == newItem.idOwnerWork
                    && oldItem.timer == newItem.timer
                    && oldItem.isCheckDone == newItem.isCheckDone
                    && oldItem.hashTag == newItem.hashTag
        }

        override fun getChangePayload(oldItem: ContentDataEntity, newItem: ContentDataEntity): Any? {
            val payloads = ArrayList<Any>()
            if (oldItem.idOwnerWork != newItem.idOwnerWork) {
                payloads.add(PAYLOAD_ID_WORK)
            }
            if (oldItem.timer != newItem.timer) {
                payloads.add(PAYLOAD_TIMER)
            }
            if (oldItem.isCheckDone != newItem.isCheckDone) {
                payloads.add(PAYLOAD_CHECKED)
            }
            if (oldItem.hashTag != newItem.hashTag) {
                payloads.add(PAYLOAD_HASH_TAG)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private val _delay: Long = 800
    private var _timer = Timer()

    private var _isShowKeyboard: Boolean = false

    private val _viewBinderHelper = ViewBinderHelper()

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        val view = parent.inflate(R.layout.item_content_check)
        setOnEditorListener(view)
        setOnClickItemListener(view)
        return view
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: ContentDataEntity, payloads: MutableList<Any>) {
        super.bind(holder, view, viewType, position, item, payloads)
        view.tag = item
        if (payloads.contains(PAYLOAD_FOCUS)) {
            if (position == currentList.size - 1 && item.name.isBlank() && currentList.size - 1 >= 0) {
                view.tvContentCheck.requestFocus()
                KeyboardUtils.showKeyboard(view.context)
            } else {
                view.tvContentCheck.clearFocus()
            }
        }
        if (payloads.contains(PAYLOAD_TIMER)) {
            refreshTvTimer(view, item)
        }
        if (payloads.contains(PAYLOAD_CHECKED)) {
            refreshCheckBox(view, item)
        }
        if (payloads.contains(PAYLOAD_HASH_TAG)) {
            refreshFlag(view, item)
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: ContentDataEntity) {
        view.tag = item
        setupViewBinderHelper(view, item)
        refreshFlag(view, item)
        refreshTvTimer(view, item)
        refreshEdtContent(view, item)
        refreshCheckBox(view, item)
    }

    private fun setupViewBinderHelper(view: View, item: ContentDataEntity) {
        _viewBinderHelper.setOpenOnlyOne(true)
        _viewBinderHelper.bind(view.swipeLayout, item.id.toString())
    }

    private fun refreshEdtContent(view: View, item: ContentDataEntity) {
        if (item.name.isBlank()) {
            view.tvContentCheck.requestFocus()
            if (!_isShowKeyboard) {
                KeyboardUtils.showKeyboard(view.context)
            }
            _isShowKeyboard = false
        }

        view.tvContentCheck.setText(item.name)
        view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
        view.tvContentCheck.underLine()
        view.tvContentCheck.setMultiLineCapSentencesAndDoneAction()
    }

    private fun refreshFlag(view: View, item: ContentDataEntity) {
        view.imgFlag.visibleOrGone(item.hashTag)
    }

    private fun refreshTvTimer(view: View, item: ContentDataEntity) {
        view.tvTimer.visibleOrGone(item.timer >= 0)
        if (TimestampUtils.compareDate(item.timer, System.currentTimeMillis())) {
            view.tvTimer.text = String.format("%s %s", TimestampUtils.getTime(item.timer), view.context.getString(R.string.title_home_today))
        } else {
            view.tvTimer.text = TimestampUtils.getFullFormatTime(item.timer, INCREASE_DATE_FORMAT)
        }
    }

    private fun refreshCheckBox(view: View, item: ContentDataEntity) {
        view.rbChecked.isChecked = item.isCheckDone
        if (item.isCheckDone) {
            view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.bg_gray))
            view.tvContentCheck.underLine()
        } else {
            view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
            view.tvContentCheck.removeUnderLine()
        }
    }

    private fun setOnClickItemListener(view: View) {
        view.imgTimer.setOnClickListenerBlock {
            (view.tag as? ContentDataEntity)?.let { item ->
                val itemById = currentList.filter { it.id == item.id }.getFirstOrNull()
                itemById?.let { it ->
                    ContentDataEntity(
                        it.id, it.name,
                        it.idOwnerWork, it.hashTag,
                        it.timer, it.isCheckDone
                    ).let {
                        moreActionClick.invoke(it, TYPE_TIMER_CLICK)
                        refreshTvTimer(view, it)
                        Handler().postDelayed({
                            view.swipeLayout.close(true)
                        }, 1000)
                    }
                }
            }
        }

        view.imgGim.setOnClickListenerBlock {
            (view.tag as? ContentDataEntity)?.let { item ->
                val itemById = currentList.filter { it.id == item.id }.getFirstOrNull()
                itemById?.copy()?.let {
                    it.hashTag = !it.hashTag
                    moreActionClick.invoke(it, TYPE_TAG_CLICK)
                    refreshFlag(view, it)
                    view.swipeLayout.close(true)
                }
            }
        }

        view.imgDelete.setOnClickListenerBlock {
            (view.tag as? ContentDataEntity)?.let { item ->
                val itemById = currentList.filter { it.id == item.id }.getFirstOrNull()
                itemById?.let {
                    moreActionClick.invoke(it, TYPE_DELETE_CLICK)
                    view.swipeLayout.close(true)
                }
            }
        }
    }

    private fun setOnEditorListener(view: View) {
        view.rbChecked.setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed) {
                (view.tag as? ContentDataEntity)?.let { item ->
                    if (isChecked && view.tvContentCheck.text.toString().isNotEmpty()) {
                        _timer = Timer()
                        view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.bg_gray))
                        view.tvContentCheck.underLine()
                        _timer.schedule(
                            object : TimerTask() {
                                override fun run() {
                                    item.copy().apply {
                                        this.isCheckDone = true
                                        handlerCheckItem.invoke(this)
                                    }
                                }
                            },
                            _delay
                        )
                    } else {
                        view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
                        view.tvContentCheck.removeUnderLine()
                        item.copy().apply {
                            this.isCheckDone = false
                            handlerCheckItem.invoke(this)
                            _timer.cancel()
                            _timer.purge()
                        }
                    }
                }
            }
        }

        view.tvContentCheck.setTextChangedListener {
            (view.tag as? ContentDataEntity)?.let { item ->
                if (it.isFocused && item.name != it.text.toString()) {
                    item.copy().apply {
                        this.name = it.text.toString()
                        updateNameContent(this)
                    }
                }
            }
        }

        view.tvContentCheck.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && view.tvContentCheck.text.toString().isNotEmpty()) {
                (view.tag as? ContentDataEntity)?.let { item ->
                    _isShowKeyboard = true
                    insertItemClick(item.copy().apply {
                        this.name = view.tvContentCheck.text.toString()
                    })
                }
                true
            } else {
                (view.tag as? ContentDataEntity)?.let { item ->
                    _isShowKeyboard = false
                    view.tvContentCheck.clearFocus()
                    insertItemClick(item.copy().apply {
                        this.name = view.tvContentCheck.text.toString()
                    })
                }
                false
            }
        }
    }

    companion object {
        const val PAYLOAD_FOCUS = "PAYLOAD_FOCUS"
        const val PAYLOAD_CHECKED = "PAYLOAD_CHECKED"
        const val PAYLOAD_ID_WORK = "PAYLOAD_ID_WORK"
        const val PAYLOAD_TIMER = "PAYLOAD_TIMER"
        const val PAYLOAD_HASH_TAG = "PAYLOAD_HASH_TAG"
        const val PAYLOAD_NAME = "PAYLOAD_NAME"

        const val TYPE_TIMER_CLICK = 100
        const val TYPE_TAG_CLICK = 101
        const val TYPE_DELETE_CLICK = 102
    }
}