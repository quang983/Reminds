package com.example.framework.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.framework.local.database.dao.LocalTopicGroupDao
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import com.example.framework.model.TopicGroup
import com.example.framework.model.WorkFoTopic


@Database(
    entities = [TopicGroup::class, WorkFoTopic::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getLocalTopicDao(): LocalTopicGroupDao

    abstract fun getLocalWorkFromTopicDao(): LocalWorkFromTopicDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}