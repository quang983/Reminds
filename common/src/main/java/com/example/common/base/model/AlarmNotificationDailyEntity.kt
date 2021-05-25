package com.example.common.base.model

data class AlarmNotificationDailyEntity(
    val idGroup: Long,
    val timeRemaining: Long,
    val name: String,
    val desc: String,
)
