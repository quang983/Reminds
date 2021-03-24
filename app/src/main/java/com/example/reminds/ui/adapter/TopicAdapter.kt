package com.example.reminds.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.common.base.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.common.BaseAdapter
import com.example.reminds.common.BaseViewHolder
import com.example.reminds.ui.fragment.home.HomeViewModel
import com.example.reminds.utils.inflate
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.setVisible
import kotlinx.android.synthetic.main.item_topic.view.*

class TopicAdapter(private val onClickDetail: (id: Long,title :String, view: View) -> Unit, private val deleteItemListener: (item: TopicGroupEntity) -> Unit) :
    BaseAdapter<HomeViewModel.TopicGroupViewItem>(object : DiffUtil.ItemCallback<HomeViewModel.TopicGroupViewItem>() {

        override fun areItemsTheSame(
            oldItem: HomeViewModel.TopicGroupViewItem,
            newItem: HomeViewModel.TopicGroupViewItem
        ): Boolean {
            return oldItem.topicGroupEntity.id == newItem.topicGroupEntity.id
        }

        override fun areContentsTheSame(
            oldItem: HomeViewModel.TopicGroupViewItem,
            newItem: HomeViewModel.TopicGroupViewItem
        ): Boolean {
            return oldItem.topicGroupEntity.name == newItem.topicGroupEntity.name
                    && oldItem.topicGroupEntity.isShowDone == newItem.topicGroupEntity.isShowDone
                    && oldItem.totalTask == newItem.totalTask
        }

        override fun getChangePayload(oldItem: HomeViewModel.TopicGroupViewItem, newItem: HomeViewModel.TopicGroupViewItem): Any? {
            val payloads = ArrayList<Any>()

            if (oldItem.topicGroupEntity.name != newItem.topicGroupEntity.name) {
                payloads.add(PAYLOAD_NAME)
            }
            if (oldItem.topicGroupEntity.isShowDone != newItem.topicGroupEntity.isShowDone) {
                payloads.add(PAYLOAD_SHOW_DONE)
            }
            if (oldItem.totalTask != newItem.totalTask) {
                payloads.add(PAYLOAD_COUNT_TASK)
            }

            return if (payloads.size > 0) {
                payloads
            } else {
                super.getChangePayload(oldItem, newItem)
            }
        }

    }) {
    private val viewBinderHelper = ViewBinderHelper()

    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_topic)
    }

    override fun bind(holder: BaseViewHolder,view: View, viewType: Int, position: Int, item: HomeViewModel.TopicGroupViewItem, payloads: MutableList<Any>) {
        super.bind(holder,view, viewType, position, item, payloads)
        if (payloads.contains(PAYLOAD_NAME)) {
            refreshName(view, item.topicGroupEntity)
        }
        if (payloads.contains(PAYLOAD_COUNT_TASK)) {
            refreshCountTask(view, item.totalTask)
        }
    }

    override fun bind(holder: BaseViewHolder, view: View, viewType: Int, position: Int, item: HomeViewModel.TopicGroupViewItem) {
        val topic = item.topicGroupEntity
        ViewCompat.setTransitionName(view.layoutRootTopic, topic.id.toString())
        refreshName(view, topic)
        refreshCountTask(view, item.totalTask)
        view.viewDivider.setVisible(position != 0)
        setupViewBinderHelper(view, topic)
        setOnClickListener(view, topic)
    }


    private fun setupViewBinderHelper(view: View, item: TopicGroupEntity) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(view.swipeLayout, item.id.toString())
    }

    private fun setOnClickListener(view: View, topic: TopicGroupEntity) {
        view.layoutRootTopic.setOnClickListener {
            onClickDetail.invoke(topic.id,topic.name, view.layoutRootTopic)
        }
        view.imgDelete.setOnClickListenerBlock {
            deleteItemListener.invoke(topic)
        }
    }

    private fun refreshName(view: View, item: TopicGroupEntity) {
        view.tvContent.text = item.name
    }

    private fun refreshCountTask(view: View, total: Int) {
        view.tvCountTask.text = total.toString()
    }

    companion object {
        const val PAYLOAD_NAME = "PAYLOAD_NAME"
        const val PAYLOAD_SHOW_DONE = "PAYLOAD_SHOW_DONE"
        const val PAYLOAD_COUNT_TASK = "PAYLOAD_COUNT_TASK"
    }
}