package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.*
import kotlinx.android.synthetic.main.item_work_group.view.*
import kotlinx.android.synthetic.main.item_work_upcoming.view.*
import kotlinx.android.synthetic.main.item_work_upcoming.view.imgFocus
import kotlinx.android.synthetic.main.item_work_upcoming.view.imgSetting
import kotlinx.android.synthetic.main.item_work_upcoming.view.swipeLayout
import kotlinx.android.synthetic.main.item_work_upcoming.view.tvDescription
import kotlinx.android.synthetic.main.item_work_upcoming.view.tvTitle

class ListWorkUpcomingAdapter(
    private val handlerCheckedAll: (workId: Long, doneAll: Boolean) -> Unit,
    private val showOptionChangeItem: (work: WorkDataEntity) -> Unit,
    private val actionFocusListener: (work: WorkDataEntity) -> Unit
) :
    BaseAdapter<WorkDataEntity>(object : DiffUtil.ItemCallback<WorkDataEntity>() {

        override fun areItemsTheSame(
            oldItem: WorkDataEntity,
            newItem: WorkDataEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: WorkDataEntity,
            newItem: WorkDataEntity
        ): Boolean {
            return oldItem.name == newItem.name && oldItem.groupId == newItem.groupId
                    && oldItem.isShowContents == newItem.isShowContents
                    && oldItem.timerReminder == newItem.timerReminder
                    && oldItem.doneAll == newItem.doneAll
                    && oldItem.description == newItem.description
        }

        override fun getChangePayload(oldItem: WorkDataEntity, newItem: WorkDataEntity): Any? {
            val payloads = ArrayList<Any>()
            if (oldItem.name != newItem.name) {
                payloads.add(PAYLOAD_NAME)
            }
            if (oldItem.groupId != newItem.groupId) {
                payloads.add(PAYLOAD_GROUP_ID)
            }
            if (oldItem.doneAll != newItem.doneAll) {
                payloads.add(PAYLOAD_DONE_ALL)
            }
            if (oldItem.timerReminder != newItem.timerReminder) {
                payloads.add(PAYLOAD_TIMER)
            }
            if (oldItem.description != newItem.description) {
                payloads.add(PAYLOAD_DESCRIPTION)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private val _viewBinderHelper = ViewBinderHelper()

    private val colorBg: IntArray = intArrayOf(
        R.color.bg_card_1, R.color.bg_card_2,
        R.color.bg_card_3, R.color.bg_card_4, R.color.bg_card_5
    )

    private val colorTick: IntArray = intArrayOf(
        R.color.bg_tick_1, R.color.bg_tick_2,
        R.color.bg_tick_3, R.color.bg_tick_4, R.color.bg_tick_5
    )

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        val view = parent.inflate(R.layout.item_work_upcoming)
        handleListener(view)
        return view
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity, payloads: MutableList<Any>) {
        super.bind(holder, view, viewType, position, item, payloads)
        view.tag = item

        if (payloads.contains(PAYLOAD_NAME)) {
            refreshTextTitle(view, item)
        }

        if (payloads.contains(PAYLOAD_DONE_ALL)) {
            refreshCheckBox(view, item)
        }

        if (payloads.contains(PAYLOAD_TIMER)) {
            refreshTimer(view, item)
        }

        if (payloads.contains(PAYLOAD_DESCRIPTION)) {
            refreshDesc(view, item)
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tag = item
        holder.itemView.isActivated = item.isShowContents
        randomColor(view, position)
        setupViewBinderHelper(view, item)
        refreshTimer(view, item)
        refreshTextTitle(view, item)
        refreshCheckBox(view, item)
        refreshDesc(view, item)
    }

    private fun setupViewBinderHelper(view: View, item: WorkDataEntity) {
        _viewBinderHelper.setOpenOnlyOne(true)
        _viewBinderHelper.bind(view.swipeLayout, item.id.toString())
    }

    private fun randomColor(view: View, position: Int) {
        when (position % 10) {
            0 -> {
                setColor(view, colorBg[0],colorTick[0])
            }
            1 -> {
                setColor(view, colorBg[1],colorTick[1])
            }
            2 -> {
                setColor(view, colorBg[2],colorTick[2])
            }
            3 -> {
                setColor(view, colorBg[3],colorTick[3])
            }
            4 -> {
                setColor(view, colorBg[4],colorTick[4])
            }
            5 -> {
                setColor(view, colorBg[0],colorTick[0])
            }
            6 -> {
                setColor(view, colorBg[1],colorTick[1])
            }
            7 -> {
                setColor(view, colorBg[2],colorTick[2])
            }
            8 -> {
                setColor(view, colorBg[3],colorTick[3])
            }
            9 -> {
                setColor(view, colorBg[4],colorTick[4])
            }
        }
    }

    private fun setColor(view: View, colorBg: Int, colorTick: Int) {
        view.rbChecked.tickColor = view.resources.getColor(colorTick)
        view.rbChecked.floorUnCheckedColor = view.resources.getColor(colorTick)
        view.cardContainer.setCardBackgroundColor(view.resources.getColor(colorBg))
    }

    private fun refreshTimer(view: View, item: WorkDataEntity) {
        view.tvTimer.visibleOrGone(item.timerReminder > 0)
        if (TimestampUtils.compareDate(item.timerReminder, System.currentTimeMillis())) {
            view.tvTimer.text =String.format("%s %s", TimestampUtils.getTime(item.timerReminder), view.context.getString(R.string.title_home_today))
        } else {
            view.tvTimer.text = TimestampUtils.getFullFormatTime(item.timerReminder, TimestampUtils.INCREASE_DATE_FORMAT)
        }
    }

    private fun refreshTextTitle(view: View, item: WorkDataEntity) {
        view.tvTitle.text = item.name
    }

    private fun refreshCheckBox(view: View, item: WorkDataEntity) {
        view.rbChecked.isChecked = item.doneAll
        if (item.doneAll) {
            view.tvTitle.setTextColor(view.context.resources.getColor(R.color.bg_gray))
            view.tvTitle.underLine()
            view.tvDescription.setTextColor(view.context.resources.getColor(R.color.bg_gray))
            view.tvDescription.underLine()
        } else {
            view.tvTitle.setTextColor(view.context.resources.getColor(R.color.black))
            view.tvTitle.removeUnderLine()
            view.tvDescription.setTextColor(view.context.resources.getColor(R.color.black))
            view.tvDescription.removeUnderLine()
        }
    }

    private fun refreshDesc(view: View, item: WorkDataEntity) {
        view.tvDescription.visibleOrGone(item.description.isNotBlank())
        view.tvDescription.text = item.description
    }

    private fun handleListener(view: View) {
        view.imgSetting.setOnClickListenerBlock {
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                view.swipeLayout.close(true)
                showOptionChangeItem.invoke(item)
            }
        }

        view.imgFocus.setOnClickListenerBlock {
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                actionFocusListener.invoke(item)
            }
        }

        view.rbChecked.setOnCheckedChangeListener { button, isChecked ->
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                if (button.isPressed) {
                    if (isChecked) {
                        handlerCheckedAll.invoke(item.id, true)
                    } else {
                        handlerCheckedAll.invoke(item.id, false)
                    }
                }
            }
        }
    }

    companion object {
        private const val PAYLOAD_NAME = "PAYLOAD_NAME"
        private const val PAYLOAD_GROUP_ID = "PAYLOAD_GROUP_ID"
        private const val PAYLOAD_DONE_ALL = "PAYLOAD_DONE_ALL"
        private const val PAYLOAD_TIMER = "PAYLOAD_TIMER"
        private const val PAYLOAD_DESCRIPTION = "PAYLOAD_DESCRIPTION"
    }

    override fun setExpanded(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity) {
    }
}