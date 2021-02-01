package com.example.reminds.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.common.base.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.ui.adapter.TopicAdapter
import com.example.reminds.ui.fragment.newtopic.NewTopicBtsFragment
import com.example.reminds.utils.navigate
import com.example.reminds.utils.setOnClickListenerBlock
import com.example.reminds.utils.setVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_today_home.view.*


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TopicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFastTopic()
        setupUI()
        setupListener()
        observeData()
    }

    private fun setupUI() {
        adapter = TopicAdapter({
            navigate(HomeFragmentDirections.actionFirstFragmentToSecondFragment(it))
        }, {
            showAlertDeleteDialog(it)
        }).apply {
            recyclerTopic.adapter = this
        }

        layoutToday.setOnClickListener {
            navigate(
                HomeFragmentDirections
                    .actionFirstFragmentToSecondFragment(
                        viewModel.fastTopicData.value?.topicGroupEntity?.id ?: 1
                    )
            )
        }
    }

    private fun setupListener() {
        btnNewTopic.setOnClickListenerBlock {
            showBottomSheet()
        }
    }

    private fun showAlertDeleteDialog(item: TopicGroupEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.notify_title))
            .setMessage(resources.getString(R.string.content_delete_topic_title))
            .setNegativeButton(resources.getString(R.string.cancel_action)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.accept_action)) { _, _ ->
                viewModel.deleteTopicData(item)
            }
            .show()
    }

    private fun observeData() {
        with(viewModel) {
            topicsGroupDataShow.observe(viewLifecycleOwner, {
                groupList.setVisible(it.isNotEmpty())
                adapter.submitList(it)
            })
            fastTopicData.observe(viewLifecycleOwner, { topic ->
                layoutToday.tvCount.text = topic.contents.size.toString()
                if (topic.contents.isNotEmpty()) {
                    topic.contents.forEachIndexed { index, it ->
                        when (index) {
                            0 -> {
                                layoutToday.tvContent.setVisible(true)
                                layoutToday.viewDividerContent.setVisible(topic.contents.size - 1 > 0)
                                layoutToday.tvContent.text = it.name
                            }
                            1 -> {
                                layoutToday.tvContentSecond.setVisible(true)
                                layoutToday.viewDividerContentSecond.setVisible(topic.contents.size - 1 > 1)
                                layoutToday.tvContentSecond.text = it.name
                            }
                            2 -> {
                                layoutToday.tvContentThird.setVisible(true)
                                layoutToday.viewDividerContentMore.setVisible(topic.contents.size - 1 > 2)
                                layoutToday.tvContentThird.text = it.name
                            }
                            else -> {
                                layoutToday.tvContentMore.setVisible((topic.contents.size - 3) > 0)
                                layoutToday.tvContentMore.text = "+${(topic.contents.size - 3).takeIf { it > 0 }} tác vụ"
                                return@forEachIndexed
                            }
                        }
                    }
                } else {
                    layoutToday.tvContent.setVisible(true)
                    layoutToday.viewDividerContent.setVisible(false)
                    layoutToday.tvContent.text = "Bạn chưa tạo nhắc nhở"
                }
            })
        }
    }

    private fun showBottomSheet() {
        val bottomSheetFragment = NewTopicBtsFragment()
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }
}