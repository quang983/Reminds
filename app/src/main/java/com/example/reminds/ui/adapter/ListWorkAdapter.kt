package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_work_group.view.*
import java.util.*

class ListWorkAdapter(
    private val onClickDetail: (position: Int) -> Unit,
    private val insertContentToWork: (work: WorkDataEntity, workPosition: Int) -> Unit,
    private val handlerCheckItem: (work: WorkDataEntity, workPosition: Int) -> Unit
) :
    BaseAdapter<WorkDataEntity>(object : DiffUtil.ItemCallback<WorkDataEntity>() {

        override fun areItemsTheSame(
            oldItem: WorkDataEntity,
            newItem: WorkDataEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: WorkDataEntity,
            newItem: WorkDataEntity
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: WorkDataEntity, newItem: WorkDataEntity): Any? {
            val payloads = ArrayList<Any>()

            if (oldItem.listContent != newItem.listContent) {
                payloads.add("PAYLOAD_CONTENT")
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private val viewPool = RecyclerView.RecycledViewPool()
    private lateinit var contentsAdapter: ListContentCheckAdapter

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_work_group)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: WorkDataEntity, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)

        if (payloads.contains("PAYLOAD_CONTENT")) {
            (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent)
        }
    }

    override fun bind(view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tvTitle.text = item.name
        view.rootView.setOnClickListener {
            onClickDetail.invoke(position)
        }
        view.recyclerWorks.apply {
            contentsAdapter = ListContentCheckAdapter({
            }, { content ->
                if (content.name.isNotEmpty()) {
                    insertContentToWork.invoke(
                        item.apply {
                            listContent.add(content)
                        }, position
                    )
                }
            }, {
                handlerCheckItem.invoke(item, position)
            })
            adapter = contentsAdapter
            setRecycledViewPool(viewPool)
            contentsAdapter.submitList(item.listContent)
        }
    }
}