package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setOnClickListenerBlock
import kotlinx.android.synthetic.main.item_work_group.view.*

class ListWorkAdapter(
    private val onClickTitle: (position: Int) -> Unit,
    private val insertContentToWork: (content: ContentDataEntity, workPosition: Int) -> Unit,
    private val handlerCheckItem: (content: ContentDataEntity, workPosition: Int) -> Unit,
    private val updateNameContent: (content: ContentDataEntity, workPosition: Int) -> Unit,
    private val moreActionClick: (item: ContentDataEntity, type: Int, workPosition: Int) -> Unit
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
            return oldItem.name == newItem.name && oldItem.groupId == newItem.groupId
                    && oldItem.listContent.zip(newItem.listContent).all { (x, y) -> x.timer == y.timer }
                    && oldItem.listContent == newItem.listContent
        }

        override fun getChangePayload(oldItem: WorkDataEntity, newItem: WorkDataEntity): Any? {
            val payloads = ArrayList<Any>()

            if (!oldItem.listContent.zip(newItem.listContent).all { (x, y) -> x.timer == y.timer } || oldItem.listContent != newItem.listContent) {
                payloads.add(PAYLOAD_CONTENT)
            }
            if (oldItem.name != newItem.name) {
                payloads.add(PAYLOAD_NAME)
            }
            if (oldItem.groupId != newItem.groupId) {
                payloads.add(PAYLOAD_GROUP_ID)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private lateinit var contentsAdapter: ListContentCheckAdapter

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_work_group)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: WorkDataEntity, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)
        if (payloads.contains(PAYLOAD_CONTENT)) {
            (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent)
        }
        if (payloads.contains(PAYLOAD_NAME)) {
            view.tvTitle.text = item.name
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tvTitle.text = item.name
        view.rootView.setOnClickListenerBlock {
            onClickTitle.invoke(position)
        }
        view.recyclerWorks.apply {
            contentsAdapter = ListContentCheckAdapter({
            }, { content ->
                if (content.name.isNotEmpty()) {
                    insertContentToWork.invoke(
                        content, position
                    )
                }
            }, { content ->
                handlerCheckItem.invoke(content, position)
            }, { content ->
                updateNameContent.invoke(content, position)
            }, { item, type ->
                moreActionClick.invoke(item, type, position)
            })
            adapter = contentsAdapter
            contentsAdapter.submitList(item.listContent.toMutableList())
        }
    }

    companion object {
        const val PAYLOAD_CONTENT = "PAYLOAD_CONTENT"
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_GROUP_ID = "PAYLOAD_GROUP_ID"
    }
}