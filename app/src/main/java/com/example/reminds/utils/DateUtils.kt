package com.example.reminds.utils

import com.fasterxml.jackson.databind.util.StdDateFormat
import java.util.*

/**
 * chuyển time từ cá định dang IOS8601 sang long
 */
object DateUtils {
    fun String.toMilisTime(): Long = StdDateFormat().apply {
        timeZone = TimeZone.getDefault()
    }.runCatching {
        parse(this@toMilisTime)?.time ?: 0L
    }.getOrElse {
        0L
    }
}
