package com.example.data.local.model

import androidx.room.Entity

@Entity
data class TopicDataModel(
    val id: Long,
    val name: String
)