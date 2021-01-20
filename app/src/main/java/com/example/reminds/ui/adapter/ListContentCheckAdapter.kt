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
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.setVisible
import kotlinx.android.synthetic.main.item_content_check.view.*
import java.util.*


class ListContentCheckAdapter(
    private val onClickDetail: (id: Long) -> Unit,
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
            return oldItem.isFocus == newItem.isFocus && oldItem.name == newItem.name
                    && oldItem.idOwnerWork == newItem.idOwnerWork && oldItem.timer == newItem.timer
                    && oldItem.isCheckDone == newItem.isCheckDone

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
            if (oldItem.isCheckDone != newItem.isCheckDone) {
                payloads.add(PAYLOAD_CHECKED)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private val DELAY: Long = 2000
    private var timer = Timer()
    private var isShowKeyboard: Boolean = false
    private val viewBinderHelper = ViewBinderHelper()

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        val view = parent.inflate(R.layout.item_content_check)
        return view
    }

    override fun bind(view: View, viewType: Int, position: Int, item: ContentDataEntity, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)
        if (payloads.contains(PAYLOAD_FOCUS)) {
            if (position == currentList.size - 1 && item.isFocus && currentList.size - 1 >= 0) {
                view.tvContentCheck.requestFocus()
            }
        }
        if (payloads.contains(PAYLOAD_NAME)) {
            refreshEdtContent(view, item)
        }
        if (payloads.contains(PAYLOAD_TIMER)) {
            refreshTvTimer(view, item)
        }
        if (payloads.contains(PAYLOAD_CHECKED)) {
            refreshCheckBox(view, item)
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: ContentDataEntity) {
        setupViewBinderHelper(view, item)
        refreshTvTimer(view, item)
        refreshEdtContent(view, item)
        refreshCheckBox(view, item)
        setOnClickItemListener(view, item)
        setOnEditorListener(view, item)
    }

    private fun setupViewBinderHelper(view: View, item: ContentDataEntity) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(view.swipeLayout, item.id.toString())
    }

    private fun refreshEdtContent(view: View, item: ContentDataEntity) {
        if (item.isFocus) {
            view.tvContentCheck.requestFocus()
        }
        view.tvContentCheck.setText(item.name)
        view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
    }

    private fun refreshTvTimer(view: View, item: ContentDataEntity) {
        view.tvTimer.setVisible(item.timer >= 0)
        view.tvTimer.text = TimestampUtils.convertMinuteToTimeStr(item.timer).toString()
    }

    private fun refreshCheckBox(view: View, item: ContentDataEntity) {
        view.rbChecked.setChecked(item.isCheckDone)
        view.tvContentCheck.setTextColor(
            if (item.isCheckDone) view.context.resources.getColor(R.color.bg_gray) else
                view.context.resources.getColor(R.color.black)
        )
        view.rbChecked.setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed) {
                item.isFocus = false
                if (isChecked && view.tvContentCheck.text.toString().isNotEmpty()) {
                    timer = Timer()
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.bg_gray))
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                item.isCheckDone = true
                                handlerCheckItem.invoke(item)
                            }
                        },
                        DELAY
                    )
                } else {
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
                    item.isCheckDone = false
                    handlerCheckItem.invoke(item)
                    timer.cancel()
                    timer.purge()
                }
            }
        }
    }

    private fun setOnClickItemListener(view: View, item: ContentDataEntity) {
        view.rootView.setOnClickListenerBlock {
            item.let {
                onClickDetail.invoke(it.id)
            }
        }

        view.imgTimer.setOnClickListenerBlock {
            item.let {
                moreActionClick.invoke(it, TYPE_TIMER_CLICK)
                Handler().postDelayed({
                    view.swipeLayout.close(true)
                }, 1000)
            }
        }

        view.imgGim.setOnClickListenerBlock {
            item.let {
                moreActionClick.invoke(it, TYPE_TAG_CLICK)
                view.swipeLayout.close(true)
            }
        }

        view.imgDelete.setOnClickListenerBlock {
            item.let {
                moreActionClick.invoke(it, TYPE_DELETE_CLICK)
                view.swipeLayout.close(true)
            }
        }
    }

    private fun setOnEditorListener(view: View, item: ContentDataEntity) {
        view.tvContentCheck.addTextChangedListener {
            item.name = it.toString()
            updateNameContent(item)
        }

        view.tvContentCheck.setOnFocusChangeListener { _, hasFocus ->
            item.isFocus = hasFocus
        }

        view.tvContentCheck.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && view.tvContentCheck.text.toString().isNotEmpty()) {
                isShowKeyboard = true
                insertItemClick(item.apply {
                    this.name = view.tvContentCheck.text.toString()
                    this.isFocus = false
                })
                true
            } else {
                isShowKeyboard = false
                view.tvContentCheck.clearFocus()
                insertItemClick(item.apply {
                    this.name = view.tvContentCheck.text.toString()
                    this.isFocus = false
                })
                false
            }
        }
    }

    companion object {
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