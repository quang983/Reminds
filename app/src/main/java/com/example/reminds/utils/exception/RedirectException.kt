package com.example.reminds.utils.exception

import com.example.reminds.utils.annotation.ExceptionType

class RedirectException(
    override val code: Int,
    val redirect: ProcessBuilder.Redirect
) : CleanException(code, ExceptionType.REDIRECT, null)