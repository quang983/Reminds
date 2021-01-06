package com.example.reminds.ui.fragment.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.common.SwipeToDeleteCallback
import com.example.reminds.ui.adapter.TopicAdapter
import com.example.reminds.ui.fragment.newtopic.NewTopicBtsFragment
import com.example.reminds.utils.navigate
import com.example.reminds.utils.setOnClickListenerBlock
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*


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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListener()
        observeData()
    }

    private fun setupUI() {
        adapter = TopicAdapter {
            navigate(HomeFragmentDirections.actionFirstFragmentToSecondFragment(it))
        }.apply {
            recyclerTopic.adapter = this
        }
    }

    private fun setupListener() {
        btnNewTopic.setOnClickListenerBlock {
            showBottomSheet()
        }
        enableSwipeToDeleteAndUndo()
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    val item: TopicGroupEntity = adapter.currentList[position]
                    adapter.removeItem(position)
                    viewModel.deleteTopicData(item)
                    val snackbar = Snackbar
                        .make(layoutRoot, "Item was removed from the list.", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO") {
                        adapter.restoreItem(item, position)
                        viewModel.undoTopicData(item)
                        recyclerTopic.scrollToPosition(position)
                    }
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.show()
                }
            }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(recyclerTopic)
    }


    private fun observeData() {
        with(viewModel) {
            topicData.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
        }
    }

    private fun showBottomSheet() {
        val bottomSheetFragment = NewTopicBtsFragment()
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }
}