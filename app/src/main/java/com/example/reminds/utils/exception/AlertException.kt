package com.example.reminds.utils.exception

import com.example.reminds.utils.annotation.ExceptionType

class AlertException(
    override val code: Int,
    override val message: String,
    val title: String? = null
) : CleanException(code, ExceptionType.ALERT, message)