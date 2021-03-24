package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.utils.*
import kotlinx.android.synthetic.main.item_work_group.view.*

class ListWorkAdapter(
    private val onClickTitle: (work: WorkDataEntity) -> Unit,
    private val insertContentToWork: (content: ContentDataEntity, workId: Long) -> Unit,
    private val handlerCheckItem: (content: ContentDataEntity, workId: Long) -> Unit,
    private val updateNameContent: (content: ContentDataEntity, workId: Long) -> Unit,
    private val moreActionClick: (item: ContentDataEntity, type: Int, workId: Long) -> Unit,
    private val deleteWorkClick: (workId: Long) -> Unit,
    private val handlerCheckedAll: (workId: Long, doneAll: Boolean) -> Unit,
    private val updateDataChanged: (work: WorkDataEntity) -> Unit,
    private val intoSettingFragment: (work: WorkDataEntity) -> Unit
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
                    && oldItem.listContent.zip(newItem.listContent).all { (x, y) -> x.timer == y.timer && x.hashTag == y.hashTag && x.isCheckDone == y.isCheckDone }
                    && oldItem.listContent == newItem.listContent && oldItem.doneAll == newItem.doneAll
                    && oldItem.listContent.size == newItem.listContent.size && oldItem.isShowContents == newItem.isShowContents
                    && oldItem.timerReminder == newItem.timerReminder
        }

        override fun getChangePayload(oldItem: WorkDataEntity, newItem: WorkDataEntity): Any? {
            val payloads = ArrayList<Any>()

            if (oldItem.isShowContents != newItem.isShowContents) {
                payloads.add(PAYLOAD_EXPANDED)
            }

            if (!oldItem.listContent.zip(newItem.listContent).all { (x, y) -> x.timer == y.timer && x.hashTag == y.hashTag && x.isCheckDone == y.isCheckDone }
                || oldItem.listContent != newItem.listContent
            ) {
                payloads.add(PAYLOAD_CONTENT)
            }

            if (oldItem.listContent.size != newItem.listContent.size) {
                payloads.add(PAYLOAD_LIST)
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
            if (oldItem.timerReminder != newItem.timerReminder) {
                payloads.add(PAYLOAD_TIMER)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private var contentsAdapter: ListContentCheckAdapter? = null
    private val _viewBinderHelper = ViewBinderHelper()

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        val view = parent.inflate(R.layout.item_work_group)
        handleListener(view)
        return view
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity, payloads: MutableList<Any>) {
        super.bind(holder, view, viewType, position, item, payloads)
        view.tag = item

        if (payloads.contains(PAYLOAD_LIST)) {
            refreshIconExpanded(view, item)
            refreshRecyclerView(view)
        }

        if (payloads.contains(PAYLOAD_EXPANDED)) {
            refreshIconExpanded(view, item)
            refreshShowRecyclerView(view, item)
        }

        if (payloads.contains(PAYLOAD_CONTENT)) {
            refreshContentList(view, item)
            refreshCountNumber(view, item)
        }

        if (payloads.contains(PAYLOAD_NAME)) {
            refreshTextTitle(view, item)
        }

        if (payloads.contains(PAYLOAD_DONE_ALL)) {
            refreshCheckBox(view, item)
        }

        if (payloads.contains(PAYLOAD_TIMER)) {
            refreshTimer(view, item)
        }

    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity) {
        view.tag = item
        view.recyclerWorks.visibility = if (item.isShowContents) View.VISIBLE else View.GONE
        holder.itemView.isActivated = item.isShowContents
        setupViewBinderHelper(view, item)
        refreshTimer(view, item)
        refreshCountNumber(view, item)
        refreshIconExpanded(view, item)
        refreshContentList(view, item)
        refreshTextTitle(view, item)
        refreshCheckBox(view, item)
    }

    private fun setupViewBinderHelper(view: View, item: WorkDataEntity) {
        _viewBinderHelper.setOpenOnlyOne(true)
        _viewBinderHelper.bind(view.swipeLayout, item.id.toString())
    }

    private fun refreshTimer(view: View, item: WorkDataEntity) {
        view.tvTimerWork.visibleOrGone(item.timerReminder > 0)
        if (TimestampUtils.compareDate(item.timerReminder, System.currentTimeMillis())) {
            view.tvTimerWork.text = String.format("%s %s", TimestampUtils.getTime(item.timerReminder), view.context.getString(R.string.title_home_today))
        } else {
            view.tvTimerWork.text = TimestampUtils.getFullFormatTime(item.timerReminder, TimestampUtils.INCREASE_DATE_FORMAT)
        }
    }

    private fun refreshIconExpanded(view: View, item: WorkDataEntity) {
        view.imgArrow.setVisible(item.listContent.isNotEmpty())
        if (item.isShowContents) {
            view.imgArrow.setImageResource(R.drawable.ic_chevron_right)
        } else {
            view.imgArrow.setImageResource(R.drawable.ic_arrow_down)
        }
    }

    private fun refreshCountNumber(view: View, item: WorkDataEntity) {
        val count = item.listContent.count { it.isCheckDone }
        view.tvCount.visibleOrGone(item.listContent.size > 0)
        view.tvCount.text = "$count/${item.listContent.size}"
    }

    private fun refreshContentList(view: View, item: WorkDataEntity) {
        (view.recyclerWorks.adapter as? ListContentCheckAdapter)?.submitList(item.listContent.toMutableList())
    }

    private fun refreshTextTitle(view: View, item: WorkDataEntity) {
        view.tvTitle.text = item.name
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

    private fun refreshRecyclerView(view: View) {
        view.recyclerWorks.visibility = View.VISIBLE
    }

    private fun refreshShowRecyclerView(view: View, item: WorkDataEntity) {
        view.recyclerWorks.visibility = if (item.isShowContents) View.VISIBLE else View.GONE
    }

    private fun handleListener(view: View) {

        view.imgArrow.setOnClickListener {
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                item.isShowContents = !item.isShowContents
                updateDataChanged.invoke(item)
            }
        }

        view.tvTitle.setOnClickListenerBlock {
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                onClickTitle.invoke(item.apply {
                    this.isShowContents = true
                })
            }
        }

        view.imgSetting.setOnClickListenerBlock {
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                view.swipeLayout.close(true)
                intoSettingFragment.invoke(item)
            }
        }

        view.recyclerWorks.apply {
            contentsAdapter = ListContentCheckAdapter(insertItemClick = { content ->
                (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                    if (content.name.isNotEmpty()) {
                        insertContentToWork.invoke(
                            content, item.id
                        )
                    }
                }
            }, { content ->
                (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                    handlerCheckItem.invoke(content, item.id)
                }
            }, { content ->
                (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                    updateNameContent.invoke(content, item.id)
                }
            }, { content, type ->
                (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                    moreActionClick.invoke(content, type, item.id)
                }
            })
            adapter = contentsAdapter
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                contentsAdapter?.submitList(item.listContent.toMutableList())
            }
        }

    /*    view.tvTitle.setOnLongClickListener {
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                showMenu(it, R.menu.menu_work_title, item.id)
            }
            true
        }*/

        view.rbCheckedTitle.setOnCheckedChangeListener { button, isChecked ->
            (view.tag as? WorkDataEntity)?.copy()?.let { item ->
                if (button.isPressed) {
                    if (isChecked) {
                        handlerCheckedAll.invoke(item.id, true)
                    } else {
                        handlerCheckedAll.invoke(item.id, false)
                    }
                }
            }
        }
    }

    companion object {
        const val PAYLOAD_CONTENT = "PAYLOAD_CONTENT"
        const val PAYLOAD_LIST = "PAYLOAD_LIST"
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_GROUP_ID = "PAYLOAD_GROUP_ID"
        const val PAYLOAD_DONE_ALL = "PAYLOAD_DONE_ALL"
        const val PAYLOAD_EXPANDED = "PAYLOAD_EXPANDED"
        const val PAYLOAD_TIMER = "PAYLOAD_TIMER"
    }

    override fun setExpanded(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: WorkDataEntity) {
    }

}