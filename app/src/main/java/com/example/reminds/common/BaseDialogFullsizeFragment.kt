package com.example.reminds.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.reminds.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.min

abstract class BaseDialogFullsizeFragment<VB : ViewBinding> : DialogFragment() {
    protected open val screenWidthPercent = 0.85F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    lateinit var mBinding: VB

    abstract fun getViewBinding(): VB

    abstract fun setupLayout()

    abstract fun setupObserver()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = getViewBinding()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setupLayout()
        setupObserver()
    }

    override fun onResume() {
        val window = dialog?.window ?: return super.onResume()

        val size = Point()
        val display = window.windowManager.defaultDisplay
        display.getSize(size)

        val width = min(size.x * screenWidthPercent, dp2px(activity, 400F))

        window.setLayout(width.toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)

        super.onResume()
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog?.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    private fun dp2px(context: Context?, dp: Float): Float {
        if (context == null) return dp
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }
}