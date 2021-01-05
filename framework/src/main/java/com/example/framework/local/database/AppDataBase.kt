package com.example.framework.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.framework.local.database.dao.LocalTopicGroupDao
import com.example.framework.local.database.dao.LocalTopicGroupWithWorkDao
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import com.example.framework.local.database.model.ContentFoWork
import com.example.framework.local.database.model.TopicGroup
import com.example.framework.local.database.model.WorkFoTopic


@Database(
    entities = [TopicGroup::class, WorkFoTopic::class, ContentFoWork::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getLocalTopicDao(): LocalTopicGroupDao

    abstract fun getLocalWorkFromTopicDao(): LocalWorkFromTopicDao

    abstract fun getLocalTopicWithWorkDao(): LocalTopicGroupWithWorkDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}