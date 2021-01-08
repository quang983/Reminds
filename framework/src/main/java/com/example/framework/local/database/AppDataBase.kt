package com.example.framework.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.framework.local.database.convert.DataConverter
import com.example.framework.local.database.dao.*
import com.example.framework.local.database.model.ContentFoWork
import com.example.framework.local.database.model.TopicGroup
import com.example.framework.local.database.model.WorkFoTopic


@Database(
    entities = [TopicGroup::class, WorkFoTopic::class, ContentFoWork::class],
    version = 1
)
@TypeConverters(DataConverter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getLocalTopicDao(): LocalTopicGroupDao

    abstract fun getLocalWorkFromTopicDao(): LocalWorkFromTopicDao

    abstract fun getLocalTopicWithWorkDao(): LocalTopicGroupWithWorkDao

    abstract fun getContentFromWorkDao(): LocalContentFromWorkDao

    abstract fun getWorkWithChildDao(): LocalWorkWithChildDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}