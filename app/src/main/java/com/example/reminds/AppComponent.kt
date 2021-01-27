package com.example.reminds

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppComponent : Application(){
    companion object{
        lateinit var shared : AppComponent
    }

    override fun onCreate() {
        shared = this
        super.onCreate()
    }
}