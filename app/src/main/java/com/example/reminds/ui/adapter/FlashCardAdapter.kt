package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder

class FlashCardAdapter() : BaseAdapter<Int>(object : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return false
    }
}) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        TODO("Not yet implemented")
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: Int) {
        TODO("Not yet implemented")
    }

}