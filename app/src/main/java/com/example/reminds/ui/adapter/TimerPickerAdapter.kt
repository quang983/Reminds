package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_timer_picker.view.*

class TimerPickerAdapter : BaseAdapter<Int>(object : DiffUtil.ItemCallback<Int>() {
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
        return parent.inflate(R.layout.item_timer_picker)
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: Int) {
        if(item <=120){
            view.tv_time.text = item.toString()
        }else{
            view.tv_time.text = ""
        }
    }
}