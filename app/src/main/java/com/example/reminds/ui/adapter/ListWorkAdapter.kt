package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_topic.view.*
import kotlinx.android.synthetic.main.item_work_group.view.*

class ListWorkAdapter(private val onClickDetail: (id: Long) -> Unit) :
    BaseAdapter<WorkDataEntity>(object : DiffUtil.ItemCallback<WorkDataEntity>() {

        override fun areItemsTheSame(
            oldItem: WorkDataEntity,
            newItem: WorkDataEntity
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: WorkDataEntity,
            newItem: WorkDataEntity
        ): Boolean {
            return false
        }

        override fun getChangePayload(oldItem: WorkDataEntity, newItem: WorkDataEntity): Any? {
            return super.getChangePayload(oldItem, newItem)
        }

    }) {
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_work_group)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tvContent.text = item.name
        view.rootView.setOnClickListener {
            onClickDetail.invoke(item.id)
        }
        view.recyclerWorks.apply {
            val adapter = ListContentCheckAdapter {

            }
            setAdapter(adapter)
            setRecycledViewPool(viewPool)
        }
    }


}