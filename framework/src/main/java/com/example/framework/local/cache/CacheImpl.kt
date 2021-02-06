package com.example.framework.local.cache

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("CommitPrefEdits")
class CacheImpl @Inject constructor(context: Context) : Cache {
    companion object {
        const val SHARED_NAME = "PREF"
        const val KEY_FIRST_LOGIN = "KEY_FIRST_LOGIN"
    }

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

 /*   private inline fun <reified T> saveData(key: String, data: T?) {
        val dataStr = when (T::class) {
            Double::class -> data?.toString()
            String::class -> data?.toString()
            Float::class -> data?.toString()
            Long::class -> data?.toString()
            Int::class -> data?.toString()
            else -> data?.toJson()
        }
        sharedPreferences.putString(key, encode(dataStr))
        cacheSource.notifyDataChange(key, data)
    }

    private inline fun <reified T> getData(key: String): T? {
        val data = decode(cacheSource.getString(key, null))
        return when (T::class) {
            Double::class -> data?.toDouble() as? T?
            String::class -> data?.toString() as? T?
            Float::class -> data?.toFloat() as? T?
            Long::class -> data?.toLong() as? T?
            Int::class -> data?.toInt() as? T?
            else -> data?.toObject(T::class.java)
        }
    }*/

/*
    private fun encode(data: String?): String? {
        if (data.isNullOrEmpty()) return null
        return encodeBase64(encodeMd5(App.shared.packageName) + encodeBase64(data))
    }

    private fun decode(data: String?): String? {
        if (data.isNullOrEmpty()) return null
        return decodeBase64(decodeBase64(data).replace(encodeMd5(App.shared.packageName), ""))
    }
*/

    private fun encodeMd5(data: String): String {
        val md = MessageDigest.getInstance("MD5")
        val hashInBytes = md.digest(data.toByteArray())

        val sb = StringBuilder()
        for (b in hashInBytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
/*
    private fun encodeBase64(data: String): String {
        return String(Base64.encode(data.toByteArray(), Base64.DEFAULT))
    }

    private fun decodeBase64(data: String): String {
        return String(Base64.decode(data.toByteArray(), Base64.DEFAULT))
    }*/


    override fun contains(key: String): Boolean = sharedPreferences.contains(key)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int): Int =
        sharedPreferences.getInt(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long): Long =
        sharedPreferences.getLong(key, defaultValue)

    override fun getFloat(key: String, defaultValue: Float): Float =
        sharedPreferences.getFloat(key, defaultValue)

    override fun getString(key: String, defaultValue: String?): String? =
        sharedPreferences.getString(key, defaultValue)

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun remove(key: String) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    override fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value)
    }
}