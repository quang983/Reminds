package com.example.reminds.ui.fragment.detail

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.base.model.ContentDataEntity
import com.example.common.base.model.WorkDataEntity
import com.example.reminds.R
import com.example.reminds.ui.adapter.ListWorkAdapter
import com.example.reminds.utils.navigateUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list_work.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class ListWorkFragment : Fragment() {
    private val args by navArgs<ListWorkFragmentArgs>()

    private val viewModel: ListWorkViewModel by viewModels()
    private lateinit var adapter: ListWorkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getListWork(args.idGroup)
        return inflater.inflate(R.layout.fragment_list_work, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupUI()
        observeData()
        setupListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val list = adapter.currentList.map { it ->
            WorkDataEntity(it.work.id, it.work.name, it.work.groupId, it.listContent.filter { it.content.id != 0L || it.content.name.isNotBlank() }.map {
                ContentDataEntity(it.content.id, it.content.name, it.content.idOwnerWork, it.content.isChecked)
            } as ArrayList<ContentDataEntity>)
        }
        viewModel.insertWorksObject(list)
    }

    private fun setupListener() {
        extendedFab.setOnClickListener {
            showDialogInputWorkTopic()
        }
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> true
            android.R.id.home -> {
                navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        adapter = ListWorkAdapter({

        }, { content, work, workPosition ->
            viewModel.insertContentToWork(content, work, workPosition)
        }, { isChecked, item ->
            viewModel.handlerCheckItem(item)
        }).apply {
            recyclerWorks.adapter = this
        }
    }

    private fun observeData() {
        with(viewModel) {
            listWorkData.observe(viewLifecycleOwner, {
                adapter.submitList(it.toMutableList())
            })
        }
    }

    private fun showDialogInputWorkTopic() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Title")
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val text = input.text.toString()
            viewModel.insertWork(text)

        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }
}