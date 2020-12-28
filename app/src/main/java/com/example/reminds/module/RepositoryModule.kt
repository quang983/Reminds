package com.example.reminds.module

import com.example.data.remote.implement.TryRepositoryImpl
import com.example.data.repository.TopicRepositoryImpl
import com.example.domain.repository.TopicRepository
import com.example.domain.repository.TryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindTryRepository(repository: TryRepositoryImpl): TryRepository

    @Binds
    abstract fun bindTopicGroupsRepository(repository: TopicRepositoryImpl): TopicRepository
}