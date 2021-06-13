package com.example.reminds.utils

import android.content.Context
import android.os.Build
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.lang.reflect.ParameterizedType
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

fun <T> Any.getClass(position: Int): Class<T> {
    val className: String = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[position].toString().split(" ")[1]
    return Class.forName(className) as Class<T>
}

fun <T> ArrayList<T>.addOrReplace(position: Int, t: T) {
    if (isNullOrEmpty()) {
        add(position, t)
    } else {
        this[0] = t
    }
}


fun <T> List<T>?.getPositionOrNull(position: Int): T? {
    return if (this != null && position >= 0 && position < this.size) {
        this[position]
    } else {
        null
    }
}

fun <T> List<T>?.getFirstOrDefault(default: T): T {
    return if (isNullOrEmpty()) {
        default
    } else {
        this!![0]
    }
}

fun <T> List<T>?.getFirstOrNull(): T? {
    return if (isNullOrEmpty()) {
        null
    } else {
        this!![0]
    }
}

fun <T> List<T>?.getLastOrNull(): T? {
    return if (isNullOrEmpty()) {
        null
    } else {
        this!![size - 1]
    }
}

fun <T> List<T>?.getOrNull(predicate: T.() -> Boolean): T? {
    this?.forEach {
        if (predicate(it)) {
            return it
        }
    }
    return null
}

fun <T> List<T>?.getOrDefault(predicate: T.() -> Boolean, default: T): T {
    this?.forEach {
        if (predicate(it)) {
            return it
        }
    }
    return default
}

inline fun <reified T> Any?.convertToList(): List<T> {
    val list = ArrayList<T>()
    if (this is T) {
        list.add(this)
    } else if (this is List<*>) {
        list.addAll(this.castList(T::class.java)!!)
    }
    return list
}

fun <T> Any?.castList(clazz: Class<T>): ArrayList<T>? {
    if (this == null) {
        return null
    }
    if (this is List<*>) {
        val result = arrayListOf<T>()
        for (o in this) {
            result.add(clazz.cast(o)!!)
        }
        return result
    }
    (this as? T)?.let {
        return arrayListOf(this as T)
    }
    throw ClassCastException()
}

fun <K, V> Any?.castMap(keyType: Class<K>, valueType: Class<V>): Map<K?, V?>? {
    if (this == null) {
        return null
    }
    if (this is Map<*, *>) {
        val result: MutableMap<K?, V?> = HashMap()
        for ((key, value) in this) {
            result[keyType.cast(key)] = valueType.cast(value)
        }
        return result
    }
    throw ClassCastException()
}

fun <K, V> Any?.castMapList(keyType: Class<K>, valueType: Class<V>): List<Map<K?, V?>?>? {
    if (this == null) {
        return null
    }
    if (this is List<*>) {
        val result: MutableList<Map<K?, V?>?> =
            ArrayList()
        for (o in this) {
            result.add(o.castMap(keyType, valueType))
        }
        return result
    }
    throw ClassCastException()
}

fun String.fromPriceStr(): Double {
    return fromDoubleStr()
}

fun String.fromPercentStr(): Double {
    return fromDoubleStr()
}

fun String.fromQuantityStr(): Double {
    return fromDoubleStr()
}

fun String.fromDoubleStr(): Double {
    val textValidate = replace(",", "")
    return when {
        textValidate.isBlank() -> 0.0
        else -> textValidate.toDouble()
    }
}

fun Double.toPriceStr(): String {
    return toDoubleStr("###,###,###,###.###")
}

fun Double.toPercentStr(): String {
    return toDoubleStr("##.##")
}

fun Double.toQuantityStr(): String {
    return toDoubleStr("###,###,###,###.###")
}

fun Double.toDoubleStr(format: String = "##.#####"): String {
    val df = DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.US))
    return df.format(this)
}

fun Long.toTimeHhMm(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.format(Date(this))
}

fun Long.toTimeHhMmSs(): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.format(Date(this))
}

fun Long.toTimeDDMMYYYY(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.format(Date(this))
}


fun Long.toISO8601(): String {
    return toTimeStr("yyyy-MM-dd'T'HH:mm:ss")
}


fun Long.toISO8601Z(): String {
    return toTimeStr("yyyy-MM-dd'T'HH:mm:ssZ")
}

fun String.fromISO8601Z(): Long {
    return fromTimeStr("yyyy-MM-dd'T'HH:mm:ssZ")
}

fun String.fromISO8601ZZ(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()

    var text = this
    val m: Matcher = Pattern.compile("\\..*?\\+").matcher(text)
    while (m.find()) {
        text = text.replace(m.group(0)?.replace("+", "") ?: "", "")
    }

    return dateFormat.parse(text)?.time ?: 0L
}

fun String.fromISO8601SSSSSSSZZ(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZZ", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.parse(this).time
}


fun String.fromISO8601ZZZZZ(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.parse(this).time
}

fun String.fromISO8601SS(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.parse(this).time
}


fun Long.toTimeStr(format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.format(this)
}

fun String.fromTimeStr(format: String): Long {
    val dateFormat = SimpleDateFormat(format, Locale.US)
    dateFormat.timeZone = TimeZone.getDefault()
    return dateFormat.parse(this).time
}

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
