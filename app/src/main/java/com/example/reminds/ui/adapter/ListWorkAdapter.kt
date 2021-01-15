package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_work_group.view.*

class ListWorkAdapter(
    private val onClickTitle: (position: Int) -> Unit,
    private val insertContentToWork: (content: ContentDataEntity, workPosition: Int, listChecked: Boolean) -> Unit,
    private val handlerCheckItem: (content: ContentDataEntity, workPosition: Int, isChecked: Boolean) -> Unit,
    private val updateNameContent: (content: ContentDataEntity, workPosition: Int, listChecked: Boolean) -> Unit,
    private val moreActionClick: (item: ContentDataEntity, type: Int, workPosition: Int, listChecked: Boolean) -> Unit
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
            return oldItem.listContent == newItem.listContent && oldItem.name == newItem.name
                    && oldItem.groupId== newItem.groupId && oldItem.listContentDone == newItem.listContentDone
        }

        override fun getChangePayload(oldItem: WorkDataEntity, newItem: WorkDataEntity): Any? {
            val payloads = ArrayList<Any>()

            if (oldItem.listContent != newItem.listContent) {
                payloads.add(PAYLOAD_CONTENT)
            }
            if (oldItem.listContentDone != newItem.listContentDone) {
                payloads.add(PAYLOAD_CONTENT_DONE)
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
    private val viewPool = RecyclerView.RecycledViewPool()
    private lateinit var contentsAdapter: ListContentCheckAdapter
    private lateinit var contentsDoneAdapter: ListContentCheckAdapter

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_work_group)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: WorkDataEntity, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)
        if (payloads.contains(PAYLOAD_CONTENT)) {
            (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent)
            (view.recyclerWorksDone.adapter as? ListContentCheckAdapter)?.submitList(item.listContentDone)
        }
        if (payloads.contains(PAYLOAD_CONTENT_DONE)) {
            (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent)
            (view.recyclerWorksDone.adapter as? ListContentCheckAdapter)?.submitList(item.listContentDone)
        }
        if (payloads.contains(PAYLOAD_NAME)) {
            view.tvTitle.text = item.name
        }
    }

    override fun bind(view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tvTitle.text = item.name
        view.rootView.setOnClickListener {
            onClickTitle.invoke(position)
        }
        view.recyclerWorks.apply {
            val isChecked = false
            contentsAdapter = ListContentCheckAdapter(isChecked, {
            }, { content ->
                if (content.name.isNotEmpty()) {
                    insertContentToWork.invoke(
                        content, position, isChecked
                    )
                }
            }, { content ->
                handlerCheckItem.invoke(content, position, isChecked)
            }, { content ->
                updateNameContent.invoke(content, position, isChecked)
            }, { item, type ->
                moreActionClick.invoke(item, type, position, isChecked)
            })
            adapter = contentsAdapter
            setRecycledViewPool(viewPool)
            contentsAdapter.submitList(item.listContent.toMutableList())
        }

        view.recyclerWorksDone.apply {
            val isChecked = true
            contentsDoneAdapter = ListContentCheckAdapter(isChecked, {
            }, { content ->
                /*if (content.name.isNotEmpty()) {
                    insertContentToWork.invoke(
                        content, index, position
                    )
                }*/
            }, { content ->
                handlerCheckItem.invoke(content, position, isChecked)
            }, { content ->
                updateNameContent.invoke(content, position, isChecked)
            }, { item, type ->
                moreActionClick.invoke(item, type, position, isChecked)
            })
            adapter = contentsDoneAdapter
            setRecycledViewPool(viewPool)
            contentsDoneAdapter.submitList(item.listContentDone.toMutableList())
        }
    }

    companion object {
        const val PAYLOAD_CONTENT = "PAYLOAD_CONTENT"
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_GROUP_ID = "PAYLOAD_GROUP_ID"
        const val PAYLOAD_CONTENT_DONE = "PAYLOAD_CONTENT_DONE"
    }
}