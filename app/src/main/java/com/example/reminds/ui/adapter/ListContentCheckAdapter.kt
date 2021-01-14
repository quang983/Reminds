package com.example.reminds.ui.adapter

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.common.base.model.ContentDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.utils.KeyboardUtils
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setVisible
import kotlinx.android.synthetic.main.item_content_check.view.*
import java.util.*


class ListContentCheckAdapter(
    private val onClickDetail: (id: Long) -> Unit,
    private val insertItemClick: (item: ContentDataEntity, position: Int) -> Unit,
    private val handlerCheckItem: (item: ContentDataEntity) -> Unit,
    private val updateNameContent: (item: ContentDataEntity) -> Unit,
    private val moreActionClick: (item: ContentDataEntity, type: Int) -> Unit
) : BaseAdapter<ContentDataEntity>(

    object : DiffUtil.ItemCallback<ContentDataEntity>() {

        override fun areItemsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
            return oldItem.isFocus == newItem.isFocus && oldItem.name == newItem.name
                    && oldItem.idOwnerWork == newItem.idOwnerWork && oldItem.timer == newItem.timer

        }

        override fun getChangePayload(oldItem: ContentDataEntity, newItem: ContentDataEntity): Any? {
            val payloads = ArrayList<Any>()
            if (oldItem.name != newItem.name) {
                payloads.add(PAYLOAD_NAME)
            }
            if (oldItem.idOwnerWork != newItem.idOwnerWork) {
                payloads.add(PAYLOAD_ID_WORK)
            }
            if (oldItem.isFocus != newItem.isFocus) {
                payloads.add(PAYLOAD_FOCUS)
            }
            if (oldItem.timer != newItem.timer) {
                payloads.add(PAYLOAD_TIMER)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private var isChangeItem: Boolean = false
    private val DELAY: Long = 2000
    var timer = Timer()
    private val viewBinderHelper = ViewBinderHelper()

    override fun getItemViewType(position: Int): Int {
        return TYPE_CHECK_ITEM
    }

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return when (viewType) {
            TYPE_ADD_ITEM -> parent.inflate(R.layout.item_view_add)
            TYPE_CHECK_ITEM -> parent.inflate(R.layout.item_content_check)
            else -> parent.inflate(R.layout.item_content_check)
        }
    }

    override fun bind(view: View, viewType: Int, position: Int, item: ContentDataEntity, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)
        if (payloads.contains(PAYLOAD_FOCUS)) {
            if (position == currentList.size - 1 && item.isFocus && currentList.size - 1 >= 0) {
                view.tvContentCheck.requestFocus()
            }
        }
        if (payloads.contains(PAYLOAD_NAME)) {
            view.tvContentCheck.setText(item.name)
        }
        if (payloads.contains(PAYLOAD_TIMER)) {
            view.tvTimer.setVisible(item.timer >= 0)
            view.tvTimer.text = TimestampUtils.convertMinuteToTimeStr(item.timer).toString()
        }
    }

    override fun bind(view: View, viewType: Int, position: Int, item: ContentDataEntity) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(view.swipeLayout, item.id.toString())
        if (viewType == TYPE_CHECK_ITEM) {
            view.tvContentCheck.setText(item.name)
            view.tvTimer.setVisible(item.timer >= 0)
            view.tvTimer.text = TimestampUtils.convertMinuteToTimeStr(item.timer).toString()

            view.rootView.setOnClickListener {
                onClickDetail.invoke(item.id)
            }

            view.imgTimer.setOnClickListener {
                moreActionClick.invoke(item, TYPE_TIMER_CLICK)
                Handler().postDelayed({
                    view.swipeLayout.close(true)
                }, 1000)
            }

            view.imgGim.setOnClickListener {
                moreActionClick.invoke(item, TYPE_TAG_CLICK)
                view.swipeLayout.close(true)
            }

            view.imgDelete.setOnClickListener {
                moreActionClick.invoke(item, TYPE_DELETE_CLICK)
                view.swipeLayout.close(true)
            }

            if (position == currentList.size - 1 && item.isFocus && currentList.size - 1 >= 0) {
                view.tvContentCheck.requestFocus()
            }
            if (isChangeItem) {
                KeyboardUtils.showKeyboard(view.tvContentCheck, view.context)
                isChangeItem = false
            }
            view.tvContentCheck.addTextChangedListener {
                item.name = it.toString()
                updateNameContent(item)
            }

            view.tvContentCheck.setOnFocusChangeListener { v, hasFocus ->
                item.isFocus = hasFocus
            }

            view.tvContentCheck.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE && view.tvContentCheck.text.toString().isNotEmpty()) {
                    insertItemClick(item.apply {
                        this.name = view.tvContentCheck.text.toString()
                        this.isFocus = false
                    }, position)
                    true
                } else {
                    view.tvContentCheck.clearFocus()
                    insertItemClick(item.apply {
                        this.name = view.tvContentCheck.text.toString()
                        this.isFocus = false
                    }, position)
                    false
                }
            }
            view.rbChecked.setOnCheckedChangeListener(null)
            view.rbChecked.setOnCheckedChangeListener { _, isChecked ->
                item.isFocus = view.tvContentCheck.isFocusable
                if (isChecked && view.tvContentCheck.text.toString().isNotEmpty()) {
                    timer = Timer()
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.bg_gray))
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                handlerCheckItem.invoke(item)
                            }
                        },
                        DELAY
                    )
                } else {
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
                    timer.cancel()
                    timer.purge()
                }
            }
            view.rbChecked.setChecked(false)
        }
    }

    companion object {
        const val TYPE_ADD_ITEM = 1
        const val TYPE_CHECK_ITEM = 2

        const val PAYLOAD_FOCUS = "PAYLOAD_FOCUS"
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_CHECKED = "PAYLOAD_CHECKED"
        const val PAYLOAD_ID_WORK = "PAYLOAD_ID_WORK"
        const val PAYLOAD_TIMER = "PAYLOAD_TIMER"

        const val TYPE_TIMER_CLICK = 100
        const val TYPE_TAG_CLICK = 101
        const val TYPE_DELETE_CLICK = 102
    }
}