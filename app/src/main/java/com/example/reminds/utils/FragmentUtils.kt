package com.example.reminds.utils

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

class FragmentUtils {
    fun Fragment.hideSoftKeyboard() {
        view?.findFocus()?.let { KeyboardUtils.hideSoftKeyboard(it, requireContext()) }
    }

    fun Fragment.navigate(direction: NavDirections, navOptions: NavOptions? = null, navExtras: Navigator.Extras? = null) {
        try {
            findNavController().navigate(direction.actionId, direction.arguments, navOptions, navExtras)
        } catch (E: Exception) {
        }
    }

    fun Fragment.finish() {
        if (!findNavController().navigateUp()) requireActivity().finish()
    }

    fun Fragment.navigateUp() {
        findNavController().navigateUp()
    }

}