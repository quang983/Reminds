package com.example.reminds.utils

import android.content.Context
import android.text.TextUtils
import com.example.reminds.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimestampUtils {
    const val ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATE_FORMAT_ = "yyyy-MM-dd"

    fun getFullFormatTime(iso: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        getCalendarFromISO(iso)?.time?.let {
            return dateFormat.format(it)
        } ?: let {
            return ""
        }
    }

    fun getFullFormatTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(Date(timestamp))
    }

    fun getTimeFormat(timeStart: Long, timeStop: Long): String {
        var start = timeStart
        var stop = timeStop
        val minuteInMillis = 1000 * 60.toLong()

        // clear seconds
        start = timeStart / minuteInMillis * minuteInMillis
        stop = timeStop / minuteInMillis * minuteInMillis
        return getTimeFormat(stop - start)
    }

    fun getTimeFormat(different: Long): String {
        var diff = different
        if (diff <= 0) return "0 phút"
        val minuteInMillis = 1000 * 60.toLong()
        val hourInMillis = minuteInMillis * 60
        val dayInMillis = hourInMillis * 24
        val elapsedDays = diff / dayInMillis
        diff = diff % dayInMillis
        val elapsedHours = diff / hourInMillis
        diff = diff % hourInMillis
        val elapsedMinutes = diff / minuteInMillis
        val time: MutableList<String> = ArrayList()
        if (elapsedDays > 0) {
            time.add("$elapsedDays ngày")
        }
        if (elapsedHours > 0) {
            time.add("$elapsedHours giờ")
        }
        if (elapsedMinutes > 0 || time.isEmpty()) {
            time.add("$elapsedMinutes phút")
        }
        return time.joinToString(separator = " ")
    }

    fun getTime(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(Date(timestamp))
    }

    fun getDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(Date(timestamp))
    }

    fun getDate(timestamp: Long, pattern: String): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(Date(timestamp))
    }

    fun getDate(iso: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        getCalendarFromISO(iso)?.time?.let {
            return dateFormat.format(it)
        } ?: let {
            return ""
        }
    }

    fun getTimestampFromNormalDate(time: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
        return try {
            val date = dateFormat.parse(time)
            date?.time ?: -1
        } catch (e: ParseException) {
            e.printStackTrace()
            -1
        }
    }

    private fun parse(pattern: String, source: String): Date? {
        return try {
            SimpleDateFormat(pattern, Locale.US).parse(source)
        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun parseISO8601(source: String): Date? {
        return parse(ISO8601_DATE_FORMAT, source)
    }

    fun getMillisFromISO8601(source: String): Long {
        return try {
            parseISO8601(source)?.time ?: -1
        } catch (e: java.lang.Exception) {
            -1
        }
    }

    fun getCalendarFromISO(dateString: String): Calendar? {
        var processingDate = dateString
        val calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.US)
        try {
            processingDate = processingDate.substring(0, 19)
            val dateFormat = SimpleDateFormat(ISO8601_DATE_FORMAT, Locale.US)
            val date = dateFormat.parse(processingDate)
            date?.let {
                calendar.time = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return calendar
    }

    fun getTimeStampFromISO(date: String?): Long {
        if (date.isNullOrBlank()) return 0
        val cal = getCalendarFromISO(date)
        return cal?.timeInMillis ?: -1
    }


    fun formatToYesterdayOrToday(context: Context, timestamp: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)
        return if (calendar[Calendar.YEAR] == today[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] == today[Calendar.DAY_OF_YEAR]) {
            context.getString(R.string.format_today) + " " + getNormalFormatTime(timestamp)
        } else if (calendar[Calendar.YEAR] == yesterday[Calendar.YEAR] && calendar[Calendar.DAY_OF_YEAR] == yesterday[Calendar.DAY_OF_YEAR]) {
            context.getString(R.string.format_yesterday) + " " + getNormalFormatTime(timestamp)
        } else {
            getNormalFormatTime(timestamp)
        }
    }

    fun getNormalFormatTime(iso: Long): String? {
        return formatByPattern(iso, "dd/MM/yyyy")
    }

    fun getNormalFormatTime(iso: String?): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        return if (getCalendarFromISO(iso!!) == null) "" else dateFormat.format(getCalendarFromISO(iso)!!.time)
    }

    fun formatByPattern(timestamp: Long, s: String): String? {
        return try {
            val simpleDateFormat = SimpleDateFormat(s, Locale.getDefault())
            simpleDateFormat.format(Date(timestamp))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun compareDate(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = date1
        cal2.timeInMillis = date2
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }

    fun getFullFormatDateHistory(timestamp: Long): String? {
        return formatByPattern(timestamp, "EEEE, dd MMM, yyyy")
    }

    fun getDisplayName(timeString: String): String? {
        return getCalendarFromISO(timeString)?.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.LONG,
            Locale.getDefault()
        )
    }

    fun getFormatTimeHoursAndMinutes(iso: String): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.US)
        dateFormat.timeZone = TimeZone.getDefault()
        return if (TextUtils.isEmpty(iso)) {
            dateFormat.format(Calendar.getInstance().time)
        } else dateFormat.format(getCalendarFromISO(iso)?.time)
    }

    fun getNormalFormatTimeBirth(iso: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try {
            val date = dateFormat.parse(iso)
            val dateFormat1 = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            dateFormat1.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            getNormalFormatTime(iso)
        }
    }

    fun getISO8601StringForDateByPattern(dateTime: String, pattern: String): String {
        val calendar = getCalendarFromISO(dateTime)
        return if (calendar != null)
            if (formatByPattern(calendar.timeInMillis, pattern) == null) ""
            else formatByPattern(calendar.timeInMillis, pattern) ?: ""
        else ""
    }

    fun getStringForCurrentCalendar(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return sdf.format(Calendar.getInstance().time)
    }

    fun getYearAndDay(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar[Calendar.YEAR] * 1000L + calendar[Calendar.DAY_OF_YEAR]
    }
}