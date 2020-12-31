package com.example.reminds.module

import android.content.Context
import androidx.room.Room
import com.example.framework.local.database.AppDataBase
import com.example.framework.local.database.dao.LocalTopicGroupDao
import com.example.framework.local.database.dao.LocalWorkFromTopicDao
import com.example.framework.model.TopicGroup
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DataBaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDataBase {
        return Room.databaseBuilder(context, AppDataBase::class.java, AppDataBase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideNoteDao(database: AppDataBase): LocalTopicGroupDao =
        database.getLocalTopicDao()

    @Singleton
    @Provides
    fun provideWorkDao(database: AppDataBase): LocalWorkFromTopicDao =
        database.getLocalWorkFromTopicDao()
}