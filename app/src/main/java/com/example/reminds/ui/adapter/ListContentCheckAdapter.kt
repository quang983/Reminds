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
    private val insertItemClick: (item: ContentDataEntity, position: Int) -> Unit,
    private val handlerCheckItem: (item: ContentDataEntity) -> Unit
) : BaseAdapter<ContentDataEntity>(

    object : DiffUtil.ItemCallback<ContentDataEntity>() {

        override fun areItemsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ContentDataEntity, newItem: ContentDataEntity): Boolean {
            return oldItem.isFocus == newItem.isFocus && oldItem.name == newItem.name && oldItem.idOwnerWork == newItem.idOwnerWork

        }

        override fun getChangePayload(oldItem: ContentDataEntity, newItem: ContentDataEntity): Any? {
            val payloads = ArrayList<Any>()
            if (oldItem.name != newItem.name) {
                payloads.add(PAYLOAD_NAME)
            }
            if (oldItem.idOwnerWork != newItem.idOwnerWork) {
                payloads.add(PAYLOAD_ID_WORK)
            }
            if (oldItem.isFocus != newItem.isFocus) {
                payloads.add(PAYLOAD_FOCUS)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private var isChangeItem: Boolean = false
    private val DELAY: Long = 1000
    val timer = Timer()


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
        if (payloads.contains(PAYLOAD_FOCUS)) {
            if (position == currentList.size - 1 && item.isFocus && currentList.size - 1 >= 0) {
                view.tvContentCheck.requestFocus()
            }
        }
        if (payloads.contains(PAYLOAD_NAME)) {
            view.tvContentCheck.setText(item.name)
        }
    }

    override fun bind(view: View, viewType: Int, position: Int, item: ContentDataEntity) {
        if (viewType == TYPE_CHECK_ITEM) {
            view.tvContentCheck.setText(item.name)
            view.rbChecked.isChecked = false
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

            view.tvContentCheck.setOnFocusChangeListener { v, hasFocus ->
                item.isFocus = hasFocus
            }

            view.tvContentCheck.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE && view.tvContentCheck.text.toString().isNotEmpty()) {
                    insertItemClick(item.apply {
                        this.name = view.tvContentCheck.text.toString()
                        this.isFocus = false
                    }, position)
                    true
                } else {
                    view.tvContentCheck.clearFocus()
                    insertItemClick(item.apply {
                        this.name = view.tvContentCheck.text.toString()
                        this.isFocus = false
                    }, position)
                    false
                }
            }

            view.rbChecked.setOnCheckedChangeListener { _, isChecked ->
                item.isFocus = view.tvContentCheck.isFocusable
                if (isChecked && view.tvContentCheck.text.toString().isNotEmpty()) {
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.bg_gray))
                    handlerCheckItem.invoke(item)
                    /*timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                item.isChecked = isChecked
                                handlerCheckItem.invoke(item, position)
                            }
                        },
                        DELAY
                    )*/
                } else {
                    view.tvContentCheck.setTextColor(view.context.resources.getColor(R.color.black))
                    handlerCheckItem.invoke(item)
                }
            }
        }
    }

    companion object {
        const val TYPE_ADD_ITEM = 1
        const val TYPE_CHECK_ITEM = 2

        const val PAYLOAD_FOCUS = "PAYLOAD_FOCUS"
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_CHECKED = "PAYLOAD_CHECKED"
        const val PAYLOAD_ID_WORK = "PAYLOAD_ID_WORK"
    }
}