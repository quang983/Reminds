package com.example.reminds.common

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class ExpandableRecyclerViewAdapter<ExpandedType : ExpandableRecyclerViewAdapter.ExpandableGroup<ExpandedType>,
        ExpandableType : ExpandableRecyclerViewAdapter.ExpandableGroup<ExpandedType>,
        PVH : ExpandableRecyclerViewAdapter.ExpandableViewHolder,
        CVH : ExpandableRecyclerViewAdapter.ExpandedViewHolder>(
    private val mExpandableList: ArrayList<ExpandableType>,
    private val expandingDirection: ExpandingDirection
) : RecyclerView.Adapter<PVH>() {

    private var expanded = false
    private var lastExpandedPosition = -1
    private var adapterAttached = false
    private var mParentRecyclerView: RecyclerView? = null
    private val mTAG = "ExpandableGroupAdapter"

    enum class ExpandingDirection {
        HORIZONTAL,
        VERTICAL
    }


    private fun initializeChildRecyclerView(childRecyclerView: RecyclerView?) {

        if (childRecyclerView != null) {

            val linearLayoutManager = LinearLayoutManager(childRecyclerView.context)

            linearLayoutManager.orientation = if (expandingDirection == ExpandingDirection.VERTICAL)
                LinearLayoutManager.VERTICAL
            else LinearLayoutManager.HORIZONTAL

            childRecyclerView.layoutManager = linearLayoutManager

        }
    }


    override fun getItemCount(): Int {
        return mExpandableList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PVH {
        return onCreateParentView(parent, viewType)
    }


    private fun onCreateParentView(parent: ViewGroup, viewType: Int): PVH {
        val pvh = onCreateParentViewHolder(parent, viewType)

        initializeChildRecyclerView(pvh.itemView.getRecyclerView())
        return pvh
    }

    fun parentClickListener(pvh: PVH) {
        val position = pvh.absoluteAdapterPosition
        if (position <= mExpandableList.size - 1) {
            val expandable = mExpandableList[position]

            if (isSingleExpanded())
                handleSingleExpansion(position)
            else handleExpansion(expandable, position)

            handleLastPositionScroll(position)

            onExpandableClick(pvh, expandable)
        }
    }

    private fun collapseAllGroups() {
        setExpanded(false)
    }

    private fun reverseExpandableState(expandableGroup: ExpandableType) {
        expandableGroup.isExpanded = !expandableGroup.isExpanded
    }

    private fun collapseAllExcept(position: Int) {
        val expandableGroup = mExpandableList[position]
        reverseExpandableState(expandableGroup)
        notifyItemChanged(position)
        if (lastExpandedPosition > -1 && lastExpandedPosition != position) {
            val previousExpandableGroup = mExpandableList[lastExpandedPosition]
            if (previousExpandableGroup.isExpanded) {
                previousExpandableGroup.isExpanded = false
                notifyItemChanged(lastExpandedPosition)
            }
        }

        lastExpandedPosition = position

    }

    private fun handleSingleExpansion(position: Int) {
        if (expanded) {
            collapseAllGroups()
        } else {
            collapseAllExcept(position)
        }
    }

    private fun handleExpansion(expandableGroup: ExpandableType, position: Int) {
        reverseExpandableState(expandableGroup)
        notifyItemChanged(position)
    }

    private fun handleLastPositionScroll(position: Int) {
        if (position == mExpandableList.lastIndex)
            mParentRecyclerView?.smoothScrollToPosition(position)
    }


    override fun onBindViewHolder(holder: PVH, position: Int) {
        setupChildRecyclerView(holder, position)
    }


    private fun setupChildRecyclerView(holder: PVH, position: Int) {
        val expandableGroup = mExpandableList[position]
        val childRecyclerView = holder.itemView.getRecyclerView()
        childRecyclerView?.adapter = ChildListAdapter(
            expandableGroup, holder, position
        ) { viewGroup, viewType ->
            onCreateChildViewHolder(viewGroup, viewType)

        }

        clickEvent(expandableGroup, holder.itemView)

        onBindParentViewHolder(holder, expandableGroup, position)
    }

    private fun clickEvent(expandableGroup: ExpandableType, containerView: View) {
        val childRecyclerView = containerView.getRecyclerView()

        childRecyclerView?.visibility = if (expandableGroup.isExpanded)
            View.VISIBLE
        else View.GONE
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

        adapterAttached = true

        mParentRecyclerView = recyclerView

        mParentRecyclerView?.layoutManager = LinearLayoutManager(recyclerView.context)

        Log.d(mTAG, "Attached: $adapterAttached")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        adapterAttached = false
        this.mParentRecyclerView = null
    }

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
        mExpandableList.applyExpansionState(expanded)
    }

    fun removeGroup(position: Int) {

        if (position < 0 || position > itemCount) {
            Log.e(mTAG, "Group can't be removed at position $position")
            return
        }

        mExpandableList.removeAt(position)

        if (adapterAttached)
            notifyItemRemoved(position)

        Log.d(mTAG, "Group removed at $position")

    }

    private fun List<ExpandableType>.applyExpansionState(expansionState: Boolean) {

        GlobalScope.launch(Dispatchers.IO) {
            forEach {
                it.isExpanded = expansionState


            }

            launch(Dispatchers.Main) {
                if (adapterAttached)
                    notifyItemRangeChanged(0, itemCount)
            }
        }

    }

    private fun View.getRecyclerView(): RecyclerView? {
        if (this is ViewGroup && childCount > 0) {
            forEach {
                if (it is RecyclerView) {
                    return it
                }
            }
        }
        Log.e(mTAG, "Recycler View for expanded items not found in parent layout.")
        return null
    }

    inner class ChildListAdapter(
        private val expandableGroup: ExpandableType,
        private val parentViewHolder: PVH,
        private val position: Int,
        private val onChildRowCreated: (ViewGroup, Int) -> CVH
    ) :
        RecyclerView.Adapter<CVH>() {
        private var mChildRecyclerView: RecyclerView? = null
        private val mExpandedList = expandableGroup.getExpandingItems()
        private val parentPosition = position

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CVH {
            val cvh = onChildRowCreated(parent, viewType)
            initializeChildRecyclerView(cvh.itemView.getRecyclerView())
            return cvh
        }

        fun childClickListener(position: Int) {
            if (position <= mExpandedList.size - 1) {
                val expandedType = mExpandedList[position]
                if (isSingleExpanded())
                    handleSingleExpansion(position)
                else handleChildExpansion(expandedType, position)

                handleChildLastPositionScroll(position)
            }
        }


        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

            adapterAttached = true

            mChildRecyclerView = recyclerView

            mChildRecyclerView?.layoutManager = LinearLayoutManager(recyclerView.context)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            adapterAttached = false
            this.mChildRecyclerView = null
        }


        private fun handleChildLastPositionScroll(position: Int) {
            if (position == mExpandedList.lastIndex)
                mChildRecyclerView?.smoothScrollToPosition(position)
        }


        private fun handleChildExpansion(expandableGroup: ExpandedType, position: Int) {
            reverseExpandedState(expandableGroup)
            notifyItemChanged(position)
        }

        private fun reverseExpandedState(expandableGroup: ExpandedType) {
            expandableGroup.isExpanded = !expandableGroup.isExpanded
        }


        override fun getItemCount(): Int {
            return mExpandedList.size
        }

        override fun onBindViewHolder(holder: CVH, position: Int) {
            val expanded = mExpandedList[position]
            onBindChildViewHolder(holder, expanded, expandableGroup, parentPosition, position)
            setupBabyRecyclerView(holder, position)
        }

        lateinit var babyListAdapter: BabyListAdapter

        private fun setupBabyRecyclerView(holder: CVH, position: Int) {
            val expandableGroup = mExpandedList[position]
            babyListAdapter = BabyListAdapter(
                expandableGroup, holder, parentPosition
            ) { viewGroup, viewType ->
                onCreateChildViewHolder(viewGroup, viewType)

            }
            val childRecyclerView = holder.itemView.getRecyclerView()
            childRecyclerView?.adapter = babyListAdapter

            clickChildEvent(expandableGroup, holder.itemView)
        }

        private fun clickChildEvent(expandableGroup: ExpandedType, containerView: View) {
            val childRecyclerView = containerView.getRecyclerView()

            childRecyclerView?.visibility = if (expandableGroup.isExpanded)
                View.VISIBLE
            else View.GONE

        }

    }

    inner class BabyListAdapter(
        private val expandableGroup: ExpandedType,
        private val parentViewHolder: CVH,
        private val position: Int,
        private val onChildRowCreated: (ViewGroup, Int) -> CVH
    ) :
        RecyclerView.Adapter<CVH>() {

        private val mExpandedList = expandableGroup.getExpandingItems()
        private val mParentPosition = position

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CVH {
            val cvh = onChildRowCreated(parent, viewType)
            return cvh
        }

        fun babyClickListener(cvh: CVH) {
            val position = cvh.absoluteAdapterPosition
            if (position <= mExpandedList.size - 1) {
                val expandedType = mExpandedList[position]
                onBabyClick(
                    parentViewHolder,
                    cvh,
                    expandedType,
                    expandableGroup
                )
            }
        }

        override fun getItemCount(): Int {
            return mExpandedList.size
        }

        override fun onBindViewHolder(holder: CVH, position: Int) {
            val expanded = mExpandedList[position]
            onBindBabyViewHolder(holder, expanded, expandableGroup, mParentPosition, position)
        }
    }

    abstract class ExpandableViewHolder(view: View) : RecyclerView.ViewHolder(view)

    abstract class ExpandedViewHolder(view: View) : RecyclerView.ViewHolder(view)

    abstract class BabyViewHolder(view: View) : RecyclerView.ViewHolder(view)


    abstract fun onCreateParentViewHolder(parent: ViewGroup, viewType: Int): PVH

    abstract fun onBindParentViewHolder(
        parentViewHolder: PVH,
        expandableType: ExpandableType,
        position: Int
    )

    abstract fun onCreateChildViewHolder(child: ViewGroup, viewType: Int): CVH

    abstract fun onBindChildViewHolder(
        childViewHolder: CVH,
        expandedType: ExpandedType,
        expandableType: ExpandableType,
        parentPosition: Int,
        position: Int
    )

    abstract fun onBindBabyViewHolder(
        childViewHolder: CVH,
        expandedType: ExpandedType,
        expandableType: ExpandedType,
        parentPosition: Int,
        position: Int
    )

    abstract fun onExpandableClick(
        expandableViewHolder: PVH,
        expandableType: ExpandableType
    )

    abstract fun onExpandedClick(
        expandableViewHolder: PVH,
        expandedViewHolder: CVH,
        expandedType: ExpandedType,
        expandableType: ExpandableType
    )

    abstract fun onBabyClick(
        expandedViewHolder: CVH,
        babyViewHolder: CVH,
        expandedType: ExpandedType,
        expandableType: ExpandedType
    )

    protected open fun isSingleExpanded() = false

    abstract class ExpandableGroup<out E> {
        /**
         * returns a list of provided type to be used for expansion.
         */
        abstract fun getExpandingItems(): List<E>

        /**
         *   Specifies if you want to show the UI in expanded form.
         */
        var isExpanded = false
    }


}