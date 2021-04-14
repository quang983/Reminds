package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.*
import kotlinx.android.synthetic.main.item_work_search.view.*

class ListSearchAdapter(private val idSelected: Long, private val onItemSelectListener: (item: WorkDataEntity) -> Unit) :
    com.example.reminds.common.BaseAdapter<WorkDataEntity>(object : DiffUtil.ItemCallback<WorkDataEntity>() {

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
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        val view = parent.inflate(R.layout.item_work_search)
        setOnClickListener(view)
        return view
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tag = item

        refreshTimer(view, item)
        refreshTextTitle(view, item)
        refreshCheckBox(view, item)
        refreshDesc(view, item)
    }


    private fun refreshTimer(view: View, item: WorkDataEntity) {
        view.tvTimer.visibleOrGone(item.timerReminder > 0)
        if (TimestampUtils.compareDate(item.timerReminder, System.currentTimeMillis())) {
            view.tvTimer.text = String.format("%s %s", TimestampUtils.getTime(item.timerReminder), view.context.getString(R.string.title_home_today))
        } else {
            view.tvTimer.text = TimestampUtils.getFullFormatTime(item.timerReminder, TimestampUtils.INCREASE_DATE_FORMAT)
        }
    }

    private fun refreshTextTitle(view: View, item: WorkDataEntity) {
        view.tvTitle.text = item.name
    }

    private fun refreshCheckBox(view: View, item: WorkDataEntity) {
        view.imgSelected.visibleOrInvisible(item.id == idSelected)
    }

    private fun refreshDesc(view: View, item: WorkDataEntity) {
        view.tvDescription.visibleOrGone(item.description.isNotBlank())
        view.tvDescription.text = item.description
    }

    private fun setOnClickListener(view: View) {
        view.rootView.setOnClickListenerBlock {
            (view.tag as? WorkDataEntity)?.let { item ->
                onItemSelectListener.invoke(item)
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
}