package com.example.reminds.utils.exception

import com.example.common.base.extesion.TagException
import com.example.reminds.utils.annotation.ExceptionType

class InlineException(
    override val code: Int,
    vararg val tags: TagException
) : CleanException(code, ExceptionType.INLINE, null)