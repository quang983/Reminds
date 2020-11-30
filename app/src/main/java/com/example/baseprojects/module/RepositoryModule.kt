package com.example.baseprojects.module

import com.example.data.remote.implement.TryRepositoryImpl
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
}