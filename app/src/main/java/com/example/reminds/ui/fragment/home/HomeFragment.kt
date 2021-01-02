package com.example.reminds.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.reminds.R
import com.example.reminds.ui.adapter.TopicAdapter
import com.example.reminds.ui.fragment.newtopic.NewTopicBtsFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListener()
        observeData()
    }

    private fun setupUI() {
        adapter = TopicAdapter {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }.apply {
            recyclerTopic.adapter = this
        }
    }

    private fun setupListener() {
        btnNewTopic.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun observeData() {
        with(viewModel) {
            topicData.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
        }
    }

    private fun showBottomSheet(){
        val bottomSheetFragment = NewTopicBtsFragment()
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }
}