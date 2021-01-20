package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.ui.fragment.home.HomeViewModel
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setVisible
import kotlinx.android.synthetic.main.item_topic.view.*


class TopicAdapter(private val onClickDetail: (id: Long) -> Unit) :
    BaseAdapter<HomeViewModel.TopicGroupViewItem>(object : DiffUtil.ItemCallback<HomeViewModel.TopicGroupViewItem>() {

        override fun areItemsTheSame(
            oldItem: HomeViewModel.TopicGroupViewItem,
            newItem: HomeViewModel.TopicGroupViewItem
        ): Boolean {
            return oldItem.topicGroupEntity.id == newItem.topicGroupEntity.id
        }

        override fun areContentsTheSame(
            oldItem: HomeViewModel.TopicGroupViewItem,
            newItem: HomeViewModel.TopicGroupViewItem
        ): Boolean {
            return oldItem.topicGroupEntity.name == newItem.topicGroupEntity.name
                    && oldItem.topicGroupEntity.isShowDone == newItem.topicGroupEntity.isShowDone
                    && oldItem.topicGroupEntity.startDate == newItem.topicGroupEntity.startDate
                    && oldItem.totalTask == newItem.totalTask
        }

        override fun getChangePayload(oldItem: HomeViewModel.TopicGroupViewItem, newItem: HomeViewModel.TopicGroupViewItem): Any? {
            val payloads = ArrayList<Any>()

            if (oldItem.topicGroupEntity.name != newItem.topicGroupEntity.name) {
                payloads.add(PAYLOAD_NAME)
            }
            if (oldItem.topicGroupEntity.isShowDone != newItem.topicGroupEntity.isShowDone) {
                payloads.add(PAYLOAD_SHOW_DONE)
            }
            if (oldItem.topicGroupEntity.startDate != newItem.topicGroupEntity.startDate) {
                payloads.add(PAYLOAD_START_DATE)
            }
            if (oldItem.totalTask != newItem.totalTask) {
                payloads.add(PAYLOAD_COUNT_TASK)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_topic)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: HomeViewModel.TopicGroupViewItem, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)
        if (payloads.contains(PAYLOAD_NAME)) {
            refreshName(view, item.topicGroupEntity)
        }
        if (payloads.contains(PAYLOAD_COUNT_TASK)) {
            refreshCountTask(view, item.totalTask)
        }
        if (payloads.contains(PAYLOAD_START_DATE)) {
            refreshDate(view, item.topicGroupEntity)
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: HomeViewModel.TopicGroupViewItem) {
        val topic = item.topicGroupEntity
        refreshName(view, topic)
        refreshCountTask(view, item.totalTask)
        refreshDate(view, topic)
        view.viewDivider.setVisible(position != currentList.size - 1)
        view.rootView.setOnClickListener {
            onClickDetail.invoke(topic.id)
        }
    }

    private fun refreshName(view: View, item: TopicGroupEntity) {
        view.tvContent.text = item.name
    }

    private fun refreshCountTask(view: View, total: Int) {
        view.tvCountTask.text = total.toString()
    }

    private fun refreshDate(view: View, item: TopicGroupEntity) {
        view.tvDate.setVisible(item.startDate != 0L)
        view.tvDate.text = TimestampUtils.getDate(item.startDate)
    }

    fun removeItem(position: Int) {
        val newList = ArrayList<HomeViewModel.TopicGroupViewItem>().apply {
            addAll(currentList)
        }
        newList.removeAt(position)
        submitList(newList)
    }

    fun restoreItem(item: HomeViewModel.TopicGroupViewItem, position: Int) {
        val newList = ArrayList<HomeViewModel.TopicGroupViewItem>().apply {
            addAll(currentList)
        }
        newList.add(position, item)
        submitList(newList)
    }

    companion object {
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_SHOW_DONE = "PAYLOAD_SHOW_DONE"
        const val PAYLOAD_START_DATE = "PAYLOAD_START_DATE"
        const val PAYLOAD_COUNT_TASK = "PAYLOAD_COUNT_TASK"
    }
}