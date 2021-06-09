package com.example.reminds.utils

import com.fasterxml.jackson.databind.util.StdDateFormat
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    @JvmStatic
    fun mapISO8601SSSSSSS(timeISO8601: String?): String = timeISO8601.mapISO8601SSSSSSS()

    @JvmStatic
    fun toISO8601SSSZ(time: Long): String = time.toISO8601SSSZ()

    @JvmStatic
    fun toISO8601SSSSSSS(time: Long): String = time.toISO8601SSSSSSS()

    @JvmStatic
    fun toISO8601(time: Long, format: String, zone: TimeZone): String = time.toISO8601(format, zone)

    @JvmStatic
    fun fromISO8601(text: String): Long = text.fromISO8601()

}


/**
 * map biểu thì cho việc chuyển đổi giữa các định dạng time IOS8601
 * định dạng đầu ra yyyy-MM-dd'T'HH:mm:ss.SSSSSSS
 */
fun String?.mapISO8601SSSSSSS(): String = this?.takeIf {
    it.isNotBlank()
}?.fromISO8601()?.takeIf {
    it != 0L
}?.toISO8601SSSSSSS() ?: ""


fun Long.toTimeDDMMYYYYHHmm(): String = toTime("dd/MM/yyyy HH:mm")

fun Long.toTime(format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.format(Date(this))
}

/**
 * chuyển long time sang định sang IOS8601
 */
fun Long.toISO8601SSSZ(): String = toISO8601("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"))

fun Long.toISO8601Zone(): String = toISO8601("yyyy-MM-dd'T'HH:mm:ssZ")

fun Long.toISO8601ZZ(): String = toISO8601("yyyy-MM-dd'T'HH:mm:ssZZ")

fun Long.toISO8601ZZZZZ(): String = toISO8601("yyyy-MM-dd'T'HH:mm:ssZZZZZ")

fun Long.toISO8601SSSSSSS(): String = toISO8601("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")

fun Long.toISO8601(format: String = "yyyy-MM-dd'T'HH:mm:ss", zone: TimeZone = TimeZone.getDefault()): String = SimpleDateFormat(format, Locale.US).apply {
    timeZone = zone
}.format(Date(this))


/**
 * chuyển time từ cá định dang IOS8601 sang long
 */
fun String.fromISO8601(): Long = StdDateFormat().apply {
    timeZone = TimeZone.getDefault()
}.runCatching {
    parse(this@fromISO8601)?.time ?: 0L
}.getOrElse {
    0L
}