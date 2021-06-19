package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.visibleOrGone
import kotlinx.android.synthetic.main.item_daily_task.view.*

class DailyListAdapter(val onClickItemListener: (item: DailyTaskWithDividerEntity) -> Unit) : BaseAdapter<DailyTaskWithDividerEntity>(object : DiffUtil.ItemCallback<DailyTaskWithDividerEntity>() {

    override fun areItemsTheSame(
        oldItem: DailyTaskWithDividerEntity,
        newItem: DailyTaskWithDividerEntity
    ): Boolean {
        return oldItem.dailyTask.id == newItem.dailyTask.id
    }

    override fun areContentsTheSame(
        oldItem: DailyTaskWithDividerEntity,
        newItem: DailyTaskWithDividerEntity
    ): Boolean {
        return oldItem.dailyTask.desc == newItem.dailyTask.desc && oldItem.dailyTask.name == newItem.dailyTask.name
    }

    override fun getChangePayload(oldItem: DailyTaskWithDividerEntity, newItem: DailyTaskWithDividerEntity): Any? {
        val payloads = ArrayList<Any>()
        if (oldItem.dailyTask.name != newItem.dailyTask.name) {
            payloads.add(PAYLOAD_NAME)
        }
        if (oldItem.dailyTask.remainingTime != newItem.dailyTask.remainingTime) {
            payloads.add(PAYLOAD_TIME_REMAINING)
        }
        if (oldItem.dailyTask.type != newItem.dailyTask.type) {
            payloads.add(PAYLOAD_TYPE)
        }
        return if (payloads.size > 0) {
            payloads
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_daily_task)
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: DailyTaskWithDividerEntity, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_NAME)) {
            refreshName(view, item)
        }
        if (payloads.contains(PAYLOAD_TIME_REMAINING)) {
            refreshTimeRemaining(view, item)
        }
        if (payloads.contains(PAYLOAD_TYPE)) {
            refreshType(view, item)
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: DailyTaskWithDividerEntity) {
        refreshName(view, item)
        refreshTimeRemaining(view, item)
        refreshType(view, item)

        view.tv_count.progress = calPercentDoneTask(item)
        view.rootView.setOnClickListenerBlock {
            onClickItemListener.invoke(item)
        }
    }

    private fun refreshName(view: View, item: DailyTaskWithDividerEntity) {
        view.tv_name.text = item.dailyTask.name
    }

    private fun refreshTimeRemaining(view: View, item: DailyTaskWithDividerEntity) {
        view.tv_time_remaining.visibleOrGone(item.dailyTask.remainingTime != null)
        view.tv_time_remaining.text = TimestampUtils.getTime(item.dailyTask.remainingTime ?: 0L)
    }

    private fun refreshType(view: View, item: DailyTaskWithDividerEntity) {
        view.tv_type.text = item.dailyTask.type.toString()
    }

    private fun calPercentDoneTask(item: DailyTaskWithDividerEntity): Int {
        val distance = TimestampUtils.getDistanceFromDateBetween(item.dailyTask.createTime, System.currentTimeMillis())
        val cal = (distance / 7) * item.dailyTask.listDayOfWeek.size
        return (distance - cal) / distance
    }

    companion object {
        const val PAYLOAD_NAME = "payload_name"
        const val PAYLOAD_TIME_REMAINING = "payload_time_remaining"
        const val PAYLOAD_TYPE = "payload_type"
    }
}