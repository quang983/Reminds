package com.example.reminds.utils.annotation

import androidx.annotation.IntDef
import com.example.reminds.utils.annotation.ExceptionType.Companion.ALERT
import com.example.reminds.utils.annotation.ExceptionType.Companion.DIALOG
import com.example.reminds.utils.annotation.ExceptionType.Companion.INLINE
import com.example.reminds.utils.annotation.ExceptionType.Companion.REDIRECT
import com.example.reminds.utils.annotation.ExceptionType.Companion.SNACK
import com.example.reminds.utils.annotation.ExceptionType.Companion.TOAST

@IntDef(SNACK, TOAST, INLINE, ALERT, DIALOG, REDIRECT)
annotation class ExceptionType {
    companion object {
        const val SNACK = 1
        const val TOAST = 2
        const val INLINE = 3
        const val ALERT = 4
        const val DIALOG = 5
        const val REDIRECT = 6
    }
}