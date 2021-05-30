package com.example.reminds.module

import com.example.data.remote.implement.TryRepositoryImpl
import com.example.data.repository.*
import com.example.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindTryRepository(repository: TryRepositoryImpl): TryRepository

    @Binds
    abstract fun bindTopicGroupsRepository(repository: TopicRepositoryImpl): TopicRepository

    @Binds
    abstract fun bindWorkFromTopicRepository(repository: WorkFromTopicRepositoryImpl): WorkFromTopicRepository

    @Binds
    abstract fun bindContentFromWorkRepository(repository: ContentRepositoryImpl): ContentRepository

    @Binds
    abstract fun bindDailyTaskWithDividerRepository(repository: DailyTaskRepositoryImpl) : DailyTaskRepository

    @Binds
    abstract fun bindDailyTaskDividerDoneRepository(repository: DailyDivideDoneRepositoryImpl) : DailyDivideDoneRepository
}