package com.example.reminds.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

open class BasePagerAdapter<T> : FragmentStateAdapter {

    private var getTitle: (T) -> String
    private var getFragment: (T) -> Fragment

    constructor(fragment: Fragment, getTitle: (T) -> String, getFragment: (T) -> Fragment) : super(fragment) {
        this.getTitle = getTitle
        this.getFragment = getFragment
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, getTitle: (T) -> String, getFragment: (T) -> Fragment) : super(fragmentManager, lifecycle) {
        this.getTitle = getTitle
        this.getFragment = getFragment
    }

    private var listItem: ArrayList<T> = ArrayList()

    fun add(t: T, notify: Boolean = true) {
        listItem.add(t)
        if (notify) notifyDataSetChanged()
    }

    fun bindData(list: List<T>, notify: Boolean = true) {
        listItem.clear()
        listItem.addAll(list)

        if (notify) notifyDataSetChanged()
    }

    override fun createFragment(position: Int): Fragment {
        return getFragment.invoke(listItem[position])
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    fun getPageTitle(position: Int): CharSequence? {
        return getTitle.invoke(listItem[position])
    }

}