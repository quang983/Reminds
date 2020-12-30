package com.example.reminds.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

fun Fragment.navigation(@IdRes resId: Int) {
    NavHostFragment.findNavController(this).navigate(resId)
}
