package com.example.reminds.module

import com.example.data.local.source.*
import com.example.framework.source.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {
    @Binds
    abstract fun bindLocalTopicSource(dataSource: TopicGroupSourceImpl): TopicGroupSource

    @Binds
    abstract fun bindLocalWorkFromTopicSource(dataSource: WorkFromTopicSourceImpl): WorkFromTopicSource

    @Binds
    abstract fun bindLocalContentFromWorkSource(dataSource: ContentFromWorkSourceImpl): ContentFromWorkSource

    @Binds
    abstract fun bindLocalDailyTaskWithDividerSource(dataSource: DailyTaskWithDividerSourceImpl): DailyTaskWithDividerSource

    @Binds
    abstract fun bindLocalDailyDivideDoneSource(dataSource: DivideDoneSourceImpl): DivideDoneSource

}
