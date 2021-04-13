package com.example.reminds.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.reminds.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
            .create()
    }

}