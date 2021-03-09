package com.example.reminds.ui.adapter

import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.inflate
import com.example.reminds.utils.removeUnderLine
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.underLine
import kotlinx.android.synthetic.main.item_work_group.view.*

class ListWorkAdapter(
    private val onClickTitle: (wId: Long) -> Unit,
    private val insertContentToWork: (content: ContentDataEntity, workId: Long) -> Unit,
    private val handlerCheckItem: (content: ContentDataEntity, workId: Long) -> Unit,
    private val updateNameContent: (content: ContentDataEntity, workId: Long) -> Unit,
    private val moreActionClick: (item: ContentDataEntity, type: Int, workId: Long) -> Unit,
    private val deleteWorkClick: (workId: Long) -> Unit,
    private val handlerCheckedAll: (workId: Long, doneAll: Boolean) -> Unit
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
                    && oldItem.listContent.zip(newItem.listContent).all { (x, y) ->
                x.timer == y.timer
                        && x.hashTag == y.hashTag && x.isCheckDone == y.isCheckDone
            }
                    && oldItem.listContent == newItem.listContent && oldItem.doneAll == newItem.doneAll
        }

        override fun getChangePayload(oldItem: WorkDataEntity, newItem: WorkDataEntity): Any? {
            val payloads = ArrayList<Any>()

            if (!oldItem.listContent.zip(newItem.listContent)
                    .all { (x, y) ->
                        x.timer == y.timer && x.hashTag == y.hashTag
                                && x.isCheckDone == y.isCheckDone
                    }
                || oldItem.listContent != newItem.listContent
            ) {
                payloads.add(PAYLOAD_CONTENT)
            }
            if (oldItem.name != newItem.name) {
                payloads.add(PAYLOAD_NAME)
            }
            if (oldItem.groupId != newItem.groupId) {
                payloads.add(PAYLOAD_GROUP_ID)
            }
            if (oldItem.doneAll != newItem.doneAll) {
                payloads.add(PAYLOAD_DONE_ALL)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private var contentsAdapter: ListContentCheckAdapter? = null

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        val view = parent.inflate(R.layout.item_work_group)
        view.tvTitle.setOnClickListenerBlock {
            (view.tag as? WorkDataEntity)?.let { item ->
                onClickTitle.invoke(item.id)
            }
        }
        view.recyclerWorks.apply {
            contentsAdapter = ListContentCheckAdapter(onClickDetail = {
            }, insertItemClick = { content ->
                (view.tag as? WorkDataEntity)?.let { item ->
                    if (content.name.isNotEmpty()) {
                        insertContentToWork.invoke(
                            content, item.id
                        )
                    }
                }
            }, { content ->
                (view.tag as? WorkDataEntity)?.let { item ->
                    handlerCheckItem.invoke(content, item.id)
                }
            }, { content ->
                (view.tag as? WorkDataEntity)?.let { item ->
                    updateNameContent.invoke(content, item.id)
                }
            }, { content, type ->
                (view.tag as? WorkDataEntity)?.let { item ->
                    moreActionClick.invoke(content, type, item.id)
                }
            })
            adapter = contentsAdapter
            (view.tag as? WorkDataEntity).let { item ->
                contentsAdapter?.submitList(item?.listContent?.toMutableList() ?: emptyList())
            }
        }

        view.tvTitle.setOnLongClickListener {
            (view.tag as? WorkDataEntity)?.let { item ->
                showMenu(it, R.menu.menu_work_title, item.id)
            }
            true
        }

        view.rbCheckedTitle.setOnCheckedChangeListener { button, isChecked ->
            (view.tag as? WorkDataEntity)?.let { item ->
                if (button.isPressed) {
                    if (isChecked) {
                        handlerCheckedAll.invoke(item.id, true)
                    } else {
                        handlerCheckedAll.invoke(item.id, false)
                    }
                }
            }
        }

        return view
    }

    override fun bind(view: View, viewType: Int, position: Int, item: WorkDataEntity, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)
        view.tag = item

        if (payloads.contains(PAYLOAD_CONTENT)) {
            (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent.toMutableList())
        }
        if (payloads.contains(PAYLOAD_NAME)) {
            view.tvTitle.text = item.name
        }
        if (payloads.contains(PAYLOAD_DONE_ALL)) {
            refreshCheckBox(view, item)
            (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent.toMutableList())
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tag = item
        contentsAdapter?.submitList(item.listContent.toMutableList())

        view.tvTitle.text = item.name
        refreshCheckBox(view, item)
    }

    private fun refreshCheckBox(view: View, item: WorkDataEntity) {
        view.rbCheckedTitle.isChecked = item.doneAll
        if (item.doneAll) {
            view.tvTitle.setTextColor(view.context.resources.getColor(R.color.bg_gray))
            view.tvTitle.underLine()
        } else {
            view.tvTitle.setTextColor(view.context.resources.getColor(R.color.blue_900))
            view.tvTitle.removeUnderLine()
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, workId: Long) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_delete_work -> {
                    deleteWorkClick.invoke(workId)
                }
            }
            true
        }
        popup.setOnDismissListener {
        }
        popup.show()
    }


    companion object {
        const val PAYLOAD_CONTENT = "PAYLOAD_CONTENT"
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_GROUP_ID = "PAYLOAD_GROUP_ID"
        const val PAYLOAD_DONE_ALL = "PAYLOAD_DONE_ALL"
    }
}