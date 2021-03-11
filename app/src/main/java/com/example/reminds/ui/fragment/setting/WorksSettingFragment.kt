package com.example.reminds.ui.fragment.setting

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.common.base.model.TopicGroupEntity
import com.example.reminds.R
import com.example.reminds.ui.fragment.detail.ListWorkFragment.Companion.FRAGMENT_SETTING_OPTION
import com.example.reminds.ui.fragment.detail.ListWorkFragmentArgs
import com.example.reminds.utils.navigateUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setting_works.*

@AndroidEntryPoint
class WorksSettingFragment : Fragment() {
    private val viewModel: WorksSettingViewModel by viewModels()
    private val args by navArgs<WorksSettingFragmentArgs>()

    companion object {
        const val DATA_OPTION_SELECTED = "DATA_OPTION_SELECTED"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_works, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setIdGroup(args.idGroup)
        setupToolbar()
        setupListener()
        setupObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_action_done, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_menu_done -> {
                setFragmentResult(FRAGMENT_SETTING_OPTION, bundleOf(DATA_OPTION_SELECTED to viewModel.optionSelected))
                navigateUp()
            }
            android.R.id.home -> {
                navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupToolbar() {
        setHasOptionsMenu(true)
    }

    private fun setupListener() {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioShow -> {
                    viewModel.optionSelected = TopicGroupEntity.SHOW_ALL_WORKS
                }
                R.id.radioHide -> {
                    viewModel.optionSelected = TopicGroupEntity.HIDE_DONE_WORKS
                }
                R.id.radioDelete -> {
                    viewModel.optionSelected = TopicGroupEntity.REMOVE_DONE_WORKS
                }
            }
        }
    }

    private fun setupObserver() {
        viewModel.getTopicByIdLiveData.observe(viewLifecycleOwner, {
            when (it) {
                TopicGroupEntity.SHOW_ALL_WORKS -> {
                    radioGroup.check(R.id.radioShow)
                }
                TopicGroupEntity.HIDE_DONE_WORKS -> {
                    radioGroup.check(R.id.radioHide)
                }
                TopicGroupEntity.REMOVE_DONE_WORKS -> {
                    radioGroup.check(R.id.radioDelete)
                }
            }
        })
    }
}