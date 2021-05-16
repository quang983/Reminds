package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.inflate

class DailyListAdapter : BaseAdapter<DailyTaskWithDividerEntity>(object : DiffUtil.ItemCallback<DailyTaskWithDividerEntity>() {

    override fun areItemsTheSame(
        oldItem: DailyTaskWithDividerEntity,
        newItem: DailyTaskWithDividerEntity
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: DailyTaskWithDividerEntity,
        newItem: DailyTaskWithDividerEntity
    ): Boolean {
        return false
    }
}) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_daily_task)
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: DailyTaskWithDividerEntity) {
    }
}