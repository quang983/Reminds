package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.TimestampUtils
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setVisible
import kotlinx.android.synthetic.main.item_topic.view.*


class TopicAdapter(private val onClickDetail: (id: Long) -> Unit) :
    BaseAdapter<TopicGroupEntity>(object : DiffUtil.ItemCallback<TopicGroupEntity>() {

        override fun areItemsTheSame(
            oldItem: TopicGroupEntity,
            newItem: TopicGroupEntity
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: TopicGroupEntity,
            newItem: TopicGroupEntity
        ): Boolean {
            return false
        }

        override fun getChangePayload(oldItem: TopicGroupEntity, newItem: TopicGroupEntity): Any? {
            return super.getChangePayload(oldItem, newItem)
        }

    }) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_topic)
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: TopicGroupEntity) {
        view.tvContent.text = item.name
        view.tvDate.setVisible(item.startDate != 0L)
        view.tvDate.text = TimestampUtils.getDate(item.startDate)
        view.viewDivider.setVisible(position != currentList.size - 1)
        view.rootView.setOnClickListener {
            onClickDetail.invoke(item.id)
        }
    }

    fun removeItem(position: Int) {
        val newList = ArrayList<TopicGroupEntity>().apply {
            addAll(currentList)
        }
        newList.removeAt(position)
        submitList(newList)
    }

    fun restoreItem(item: TopicGroupEntity, position: Int) {
        val newList = ArrayList<TopicGroupEntity>().apply {
            addAll(currentList)
        }
        newList.add(position, item)
        submitList(newList)
    }
}