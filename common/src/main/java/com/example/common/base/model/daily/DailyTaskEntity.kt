package com.example.common.base.model.daily

data class DailyTaskEntity(
    val id: Long,
    val name: String,
    val createTime: Long,
    val endTime: Long,
    val dailyDivideTasks: List<DailyDivideTaskEntity>
)