package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WorkFoTopic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val idOwnerGroup: Long
)
