package com.example.framework.local.cache

interface Cache {
    fun getInt(key: String, defaultValue: Int): Int

    fun getLong(key: String, defaultValue: Long): Long

    fun getString(key: String, defaultValue: String?): String?

    fun getFloat(key: String, defaultValue: Float): Float

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    operator fun contains(key: String): Boolean

    fun putInt(key: String, value: Int)

    fun putLong(key: String, value: Long)

    fun putFloat(key: String, value: Float)

    fun putString(key: String, value: String?)

    fun putBoolean(key: String, value: Boolean)

    fun remove(key: String)

    fun clear()

}