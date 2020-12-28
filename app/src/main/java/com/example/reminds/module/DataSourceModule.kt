package com.example.reminds.module

import com.example.framework.local.database.dao.LocalTopicGroupDao
import com.example.framework.source.TopicGroupSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindLocalTopicSource(dataSource: TopicGroupSourceImpl): LocalTopicGroupDao
}