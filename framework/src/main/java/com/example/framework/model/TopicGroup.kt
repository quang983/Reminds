package com.example.framework.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TopicGroup(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String
)