package com.example.common.base.model

import androidx.room.Entity

@Entity
data class ContentDataEntity(var id: Long, var name: String, var idOwnerWork: Long = 0)