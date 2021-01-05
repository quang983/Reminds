package com.example.common.base.model

import androidx.room.Entity

@Entity
data class TopicDataModel(
    val id: Long,
    val name: String
)