package com.example.reminds.utils.exception

import com.example.reminds.utils.annotation.ExceptionType

open class CleanException(
    open val code: Int,
    @ExceptionType val exceptionType: Int,
    override val message: String?
) : Throwable(message)