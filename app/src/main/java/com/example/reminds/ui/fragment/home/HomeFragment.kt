package com.example.reminds.ui.fragment.home

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.example.common.base.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.ui.adapter.TopicAdapter
import com.example.reminds.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_new_topic_bts.*
import kotlinx.android.synthetic.main.item_topic.*
import kotlinx.android.synthetic.main.item_topic.view.*
import kotlinx.android.synthetic.main.layout_custom_alert_text_input.view.*
import kotlinx.android.synthetic.main.layout_today_home.view.*
import kotlinx.android.synthetic.main.layout_today_home.view.tvContent
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TopicAdapter
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presentShowcaseView()
        setupTransition(view)
        getData()
        setupUI()
        setupListener()
        observeData()
    }

    private fun getData() {
        viewModel.getFastTopic()
    }

    private fun setupTransition(view: View) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setupUI() {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        layoutToday.transitionName = "def"
        adapter = TopicAdapter({ id, title, view ->
            val emailCardDetailTransitionName = getString(R.string.transition_topic_to_detail)
            val extras = FragmentNavigatorExtras(view to emailCardDetailTransitionName)
            navigate(HomeFragmentDirections.actionFirstFragmentToSecondFragment(id, title), null, extras)
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
        }, {
            showAlertDeleteDialog(it)
        }).apply {
            recyclerTopic.adapter = this
        }

        layoutToday.setOnClickListener {
            val emailCardDetailTransitionName = getString(R.string.transition_topic_to_detail)
            val extras = FragmentNavigatorExtras(layoutToday to emailCardDetailTransitionName)

            navigate(
                HomeFragmentDirections
                    .actionFirstFragmentToSecondFragment(
                        viewModel.fastTopicData.value?.topicGroupEntity?.id ?: 1, resources.getString(R.string.title_faster_note)
                    ), null, extras
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
                layoutEmpty.setVisible(it.isEmpty())
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
                    layoutToday.tvContent.text = getString(R.string.create_notify_new)
                }
            })
        }
    }

    private fun showBottomSheet() {
        customAlertDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_custom_alert_text_input, null, false)
        customAlertDialogView.setPadding(36.toDp, 0, 36.toDp, 0)
        customAlertDialogView.rootView.textInput.hint = getString(R.string.add_new_topic_hint)
        materialAlertDialogBuilder.setView(customAlertDialogView)
            .setTitle(resources.getString(R.string.notify_title))
            .setPositiveButton(resources.getString(R.string.add)) { _, _ ->
                customAlertDialogView.edtInput.text.toString().takeIf { it.isNotBlank() }?.let {
                    viewModel.insertTopic(it)
                } ?: Toast.makeText(requireContext(), resources.getString(R.string.warning_title_min), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show().apply {
                this.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
                this.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
            }
        customAlertDialogView.requestFocus()
        Handler().postDelayed({
            KeyboardUtils.showKeyboard(requireContext())
        }, 500)
    }


    private fun presentShowcaseView() {
        val config = ShowcaseConfig()
        config.delay = 500 // half second between each showcase view
        val sequence = MaterialShowcaseSequence(requireActivity(), "SHOWCASE_ID")

        sequence.setConfig(config)

        sequence.addSequenceItem(layoutToday, resources.getString(R.string.support_list_reminders), "GOT IT")

        sequence.addSequenceItem(
            MaterialShowcaseView.Builder(requireActivity())
                .setSkipText("SKIP")
                .setTarget(btnNewTopic)
                .setDismissText("GOT IT")
                .setContentText(resources.getString(R.string.support_new_title))
                .withRectangleShape(true)
                .build()
        )

        sequence.start()
    }
}