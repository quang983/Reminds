package com.example.reminds.module

import android.content.Context
import androidx.room.Room
import com.example.framework.local.database.AppDataBase
import com.example.framework.local.database.dao.*
import com.example.framework.source.MIGRATION_1_2
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
            .addMigrations(MIGRATION_1_2)
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

    @Singleton
    @Provides
    fun provideTopicWithWorkDao(database: AppDataBase): LocalTopicGroupWithWorkDao =
        database.getLocalTopicWithWorkDao()

    @Singleton
    @Provides
    fun provideContentFromWorkDao(database: AppDataBase): LocalContentFromWorkDao =
        database.getContentFromWorkDao()

    @Singleton
    @Provides
    fun provideWorkWithChildDao(database: AppDataBase): LocalWorkWithChildDao =
        database.getWorkWithChildDao()
}