package com.example.reminds.utils.exception

import com.example.reminds.utils.annotation.ExceptionType

class SnackBarException(
    override val code: Int,
    override val message: String
) : CleanException(code, ExceptionType.SNACK, message)