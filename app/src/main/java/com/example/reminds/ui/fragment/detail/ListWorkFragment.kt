package com.example.reminds.ui.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.reminds.R
import com.example.reminds.ui.adapter.ListWorkAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list_work.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class ListWorkFragment : Fragment() {
    private val viewModel: ListWorkViewModel by viewModels()
    private lateinit var adapter: ListWorkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_work, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
    }

    private fun setupUI() {
        adapter = ListWorkAdapter {

        }.apply {
            recyclerWorks.adapter = this
        }
    }

    private fun observeData() {
        with(viewModel) {
            listWorkData.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
    }
}