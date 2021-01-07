package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.ui.fragment.detail.ListWorkViewModel
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_work_group.view.*
import java.util.*

class ListWorkAdapter(
    private val onClickDetail: (id: Long) -> Unit,
    private val insertContentToWork: (content: ContentDataEntity, work: WorkDataEntity, workPosition: Int) -> Unit
) :
    BaseAdapter<ListWorkViewModel.WorkDataItemView>(object : DiffUtil.ItemCallback<ListWorkViewModel.WorkDataItemView>() {

        override fun areItemsTheSame(
            oldItem: ListWorkViewModel.WorkDataItemView,
            newItem: ListWorkViewModel.WorkDataItemView
        ): Boolean {
            return oldItem.work.id == newItem.work.id
        }

        override fun areContentsTheSame(
            oldItem: ListWorkViewModel.WorkDataItemView,
            newItem: ListWorkViewModel.WorkDataItemView
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: ListWorkViewModel.WorkDataItemView, newItem: ListWorkViewModel.WorkDataItemView): Any? {
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

    override fun bind(view: View, viewType: Int, position: Int, item: ListWorkViewModel.WorkDataItemView, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)

        if (payloads.contains("PAYLOAD_CONTENT")) {
            (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent)
        }
    }

    override fun bind(view: View, viewType: Int, position: Int, item: ListWorkViewModel.WorkDataItemView) {
        view.tvTitle.text = item.work.name
        view.rootView.setOnClickListener {
            onClickDetail.invoke(item.work.id)
        }
        view.recyclerWorks.apply {
            contentsAdapter = ListContentCheckAdapter({

            }, { content ->
                insertContentToWork.invoke(content.content, item.work, position)
            })
            adapter = contentsAdapter
            setRecycledViewPool(viewPool)
            contentsAdapter.submitList(item.listContent)
        }
    }
}