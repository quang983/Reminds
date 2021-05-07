package com.example.common.base.model.daily

data class DailyDivideTaskEntity(
    val id: Long,
    val name: String,
    val createTime: Long,
    val doneTime: Long,
    val remainingTime: Long
)