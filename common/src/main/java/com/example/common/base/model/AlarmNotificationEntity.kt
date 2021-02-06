package com.example.common.base.model

data class AlarmNotificationEntity(
    val timeAlarm : Long,
    val idTopic : Long,
    val idContent : Long,
    val nameContent : String,
    val nameWork : String,
)