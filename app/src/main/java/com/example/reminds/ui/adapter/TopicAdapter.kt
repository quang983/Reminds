package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.domain.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import kotlinx.android.synthetic.main.item_topic.view.*
import com.example.reminds.utils.inflate

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

    override fun bind(view: View, viewType: Int, position: Int, item: TopicGroupEntity) {
        view.tvContent.text = item.name
        view.rootView.setOnClickListener {
            onClickDetail.invoke(item.id)
        }
    }
}