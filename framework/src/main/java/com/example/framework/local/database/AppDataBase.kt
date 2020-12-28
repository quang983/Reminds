package com.example.framework.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.framework.local.database.dao.LocalTopicGroupDao
import com.example.framework.model.TopicGroup


@Database(
    entities = [TopicGroup::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getLocalTopicDao(): LocalTopicGroupDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}