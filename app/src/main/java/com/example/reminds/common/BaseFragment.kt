package com.example.reminds.common

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.reminds.R
import com.example.reminds.utils.navigateUp

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    protected lateinit var mBinding: VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = getViewBinding()
        return mBinding.root
    }

    abstract fun getViewBinding(): VB

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}