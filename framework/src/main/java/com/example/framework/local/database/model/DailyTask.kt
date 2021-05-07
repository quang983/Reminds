package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val createTime: Long,
    val endTime: Long,
    val dailyDivideTasks: List<DailyDivideTask>
)
