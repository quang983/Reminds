package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.daily.DailyTaskEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.inflate

class DailyListAdapter : BaseAdapter<DailyTaskEntity>(object : DiffUtil.ItemCallback<DailyTaskEntity>() {

    override fun areItemsTheSame(
        oldItem: DailyTaskEntity,
        newItem: DailyTaskEntity
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: DailyTaskEntity,
        newItem: DailyTaskEntity
    ): Boolean {
        return false
    }
}) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_daily_task)
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: DailyTaskEntity) {
    }

}