package com.example.common.base.model

import androidx.room.Entity

@Entity
data class ContentDataEntity(val id: Long, val name: String, var idOwnerWork: Long = 0)