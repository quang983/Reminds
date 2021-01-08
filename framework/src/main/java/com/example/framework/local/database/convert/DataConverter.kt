package com.example.framework.local.database.convert

import androidx.room.TypeConverter
import com.example.framework.local.database.model.ContentFoWork
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type


class DataConverter : Serializable {
    @TypeConverter
    fun fromContentList(contents: MutableList<ContentFoWork>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<MutableList<ContentFoWork>>() {}.type
        return gson.toJson(contents, type)
    }

    @TypeConverter
    fun toContentList(json: String): MutableList<ContentFoWork> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<ContentFoWork>>() {}.type
        return gson.fromJson(json, type)
    }
}