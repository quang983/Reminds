package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.model.ContentDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.utils.KeyboardUtils
import com.example.reminds.utils.inflate
import kotlinx.android.synthetic.main.item_content_check.view.*
import java.util.*


class ListContentCheckAdapter(
    private val onClickDetail: (id: Long) -> Unit,
    private val insertItemClick: (item: ContentDataEntity) -> Unit,
    private val handlerCheckItem: () -> Unit
) : BaseAdapter<ContentDataEntity>(

    object : DiffUtil.ItemCallback<ContentDataEntity>() {

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
    private var isChangeItem: Boolean = false
    private val DELAY: Long = 1000

    override fun getItemViewType(position: Int): Int {
        return TYPE_CHECK_ITEM
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
            if (position == currentList.size - 1 && item.isFocus && currentList.size - 1 >= 0) {
                view.tvContentCheck.requestFocus()
            }
            if (isChangeItem) {
                KeyboardUtils.showKeyboard(view.tvContentCheck, view.context)
                isChangeItem = false
            }
            view.tvContentCheck.addTextChangedListener {
                item.name = it.toString()
            }

            view.tvContentCheck.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE && view.tvContentCheck.text.toString().isNotEmpty()) {
                    insertItemClick(item.apply {
                        this.name = view.tvContentCheck.text.toString()
                    })
                    true
                } else {
                    view.tvContentCheck.clearFocus()
                    insertItemClick(item.apply {
                        this.name = view.tvContentCheck.text.toString()
                    })
                    false
                }
            }

            view.rbChecked.isChecked = item.isChecked
            view.rbChecked.setOnCheckedChangeListener { _, isChecked ->
                val timer = Timer()
                if (isChecked) {
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.bg_gray))
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                item.isChecked = isChecked
                                handlerCheckItem.invoke()
                                timer.cancel()
                            }
                        },
                        DELAY
                    )
                } else {
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
                    handlerCheckItem.invoke()
                }
            }
        }
    }

    companion object {
        const val TYPE_ADD_ITEM = 1
        const val TYPE_CHECK_ITEM = 2
    }
}