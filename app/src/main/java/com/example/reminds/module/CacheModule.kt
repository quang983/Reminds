package com.example.reminds.module

import android.content.Context
import androidx.room.Room
import com.example.framework.local.cache.Cache
import com.example.framework.local.cache.CacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class CacheModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): Cache = CacheImpl(appContext)
}