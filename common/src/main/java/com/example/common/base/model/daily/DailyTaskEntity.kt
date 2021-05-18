package com.example.common.base.model.daily

data class DailyTaskEntity(
    var id: Long,
    var name: String,
    var createTime: Long,
    var endTime: Long,
    var remainingTime: Long?,
)