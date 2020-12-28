package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.domain.model.TopicData
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import net.citigo.kiotviet.common.utils.extension.inflate

class TopicAdapter : BaseAdapter<TopicData>(object : DiffUtil.ItemCallback<TopicData>() {

    override fun areItemsTheSame(oldItem: TopicData, newItem: TopicData): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: TopicData, newItem: TopicData): Boolean {
        return false
    }

    override fun getChangePayload(oldItem: TopicData, newItem: TopicData): Any? {
        return super.getChangePayload(oldItem, newItem)
    }

}) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_topic)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: TopicData) {
    }
}