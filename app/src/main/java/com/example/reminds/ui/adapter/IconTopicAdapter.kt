package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setOnClickListenerBlock
import kotlinx.android.synthetic.main.item_icon_pick.view.*

class IconTopicAdapter(val pickIconListener: (iconResource : Int) -> Unit) : BaseAdapter<Int>(object : DiffUtil.ItemCallback<Int>() {
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
        return parent.inflate(R.layout.item_icon_pick)
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: Int) {
        Glide
            .with(view.context)
            .load(item)
            .centerInside()
            .placeholder(R.drawable.ic_plus)
            .into(view.img_icon)
        view.img_icon.setOnClickListenerBlock {
            pickIconListener.invoke(item)
        }
    }

}