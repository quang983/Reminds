package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.ContentDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_topic.view.*

class ListContentCheckAdapter(private val onClickDetail: (id: Long) -> Unit) :
    BaseAdapter<ContentDataEntity>(object : DiffUtil.ItemCallback<ContentDataEntity>() {

        override fun areItemsTheSame(
            oldItem: ContentDataEntity,
            newItem: ContentDataEntity
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: ContentDataEntity,
            newItem: ContentDataEntity
        ): Boolean {
            return false
        }

        override fun getChangePayload(
            oldItem: ContentDataEntity,
            newItem: ContentDataEntity
        ): Any? {
            return super.getChangePayload(oldItem, newItem)
        }

    }) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_content_check)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: ContentDataEntity) {
        view.tvContent.text = item.name
        view.rootView.setOnClickListener {
            onClickDetail.invoke(item.id)
        }
    }
}