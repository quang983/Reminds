package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.ContentDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.utils.KeyboardUtils
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_content_check.view.*


class ListContentCheckAdapter(
    private val onClickDetail: (id: Long) -> Unit,
    private val insertItemClick: (item: ContentDataEntity) -> Unit
) : BaseAdapter<ContentDataEntity>(object : DiffUtil.ItemCallback<ContentDataEntity>() {

    override fun areItemsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ContentDataEntity, newItem: ContentDataEntity): Any? {
        return super.getChangePayload(oldItem, newItem)
    }

}) {
    override fun getItemViewType(position: Int): Int {
        return when (currentList[position].id) {
            0L -> TYPE_ADD_ITEM
            else -> TYPE_CHECK_ITEM
        }
    }

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return when (viewType) {
            TYPE_ADD_ITEM -> parent.inflate(R.layout.item_view_add)
            TYPE_CHECK_ITEM -> parent.inflate(R.layout.item_content_check)
            else -> parent.inflate(R.layout.item_content_check)
        }
    }

    override fun bind(view: View, viewType: Int, position: Int, item: ContentDataEntity, payloads: MutableList<Any>) {
        super.bind(view, viewType, position, item, payloads)

    }

    override fun bind(view: View, viewType: Int, position: Int, item: ContentDataEntity) {
        if (viewType == TYPE_CHECK_ITEM) {
            view.tvContentCheck.setText(item.name)
            view.rootView.setOnClickListener {
                onClickDetail.invoke(item.id)
            }
            if (position == currentList.size - 1) {
                view.tvContentCheck.requestFocus()
            }
            if (currentList.size == 1) {
                KeyboardUtils.showKeyboard(view.tvContentCheck, view.context)
            }
            view.tvContentCheck.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE && view.tvContentCheck.text.toString().isNotBlank()) {
                    insertItemClick(item.apply {
                        name = view.tvContentCheck.text.toString()
                    })
                    true
                } else {
                    view.tvContentCheck.clearFocus()
                    insertItemClick(item.apply {
                        name = view.tvContentCheck.text.toString()
                    })
                    false
                }
            }
        } else if (viewType == TYPE_ADD_ITEM) {
            view.rootView.setOnClickListener {
                changeItemCheck(view, position)
            }
        }
    }

    private fun changeItemCheck(view: View, position: Int) {
        currentList[position].id = System.currentTimeMillis()
        notifyItemChanged(position)
    }

    companion object {
        const val TYPE_ADD_ITEM = 1
        const val TYPE_CHECK_ITEM = 2
    }
}