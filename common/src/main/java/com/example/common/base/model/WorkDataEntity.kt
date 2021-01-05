package com.example.common.base.model

import androidx.room.Entity

@Entity
data class WorkDataEntity(
    val id: Long,
    val name: String,
    val groupId: Long,
    val listContent : List<ContentDataEntity>
)