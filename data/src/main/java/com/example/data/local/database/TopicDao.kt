package com.example.data.local.database

import androidx.room.Dao
import androidx.room.Query
import com.example.data.local.model.TopicEntity

@Dao
interface TopicDao {
    @Query("SELECT * FROM topic")
    fun fetAll() : List<TopicEntity>
}