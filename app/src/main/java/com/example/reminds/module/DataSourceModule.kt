package com.example.reminds.module

import com.example.data.local.source.TopicGroupSource
import com.example.data.local.source.WorkFromTopicSource
import com.example.framework.source.TopicGroupSourceImpl
import com.example.framework.source.WorkFromTopicSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindLocalTopicSource(dataSource: TopicGroupSourceImpl): TopicGroupSource

    @Binds
    abstract fun bindLocalWorkFromTopicSource(dataSource: WorkFromTopicSourceImpl): WorkFromTopicSource
}