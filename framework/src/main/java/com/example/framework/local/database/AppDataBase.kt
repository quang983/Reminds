package com.example.framework.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.framework.local.database.convert.DataConverter
import com.example.framework.local.database.dao.*
import com.example.framework.local.database.model.*


@Database(
    entities = [TopicGroup::class, WorkFoTopic::class, ContentFoWork::class, DailyTask::class, DailyDivideTaskDone::class],
    version = 5, exportSchema = true
)
@TypeConverters(DataConverter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getLocalTopicDao(): LocalTopicGroupDao

    abstract fun getLocalWorkFromTopicDao(): LocalWorkFromTopicDao

    abstract fun getLocalTopicWithWorkDao(): LocalTopicGroupWithWorkDao

    abstract fun getContentFromWorkDao(): LocalContentFromWorkDao

    abstract fun getWorkWithChildDao(): LocalWorkWithChildDao

    abstract fun getDailyTaskDao(): LocalDailyTaskWithDividerDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}