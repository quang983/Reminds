package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyDivideTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val createTime: Long,
    val doneTime: Long,
    val remainingTime: Long
)