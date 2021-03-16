package com.example.reminds.utils.exception

import android.app.Dialog
import com.example.reminds.utils.annotation.ExceptionType

class DialogException (
    override val code: Int,
    val dialog: Dialog
) : CleanException(code, ExceptionType.DIALOG, null)