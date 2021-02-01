package com.example.common.base.model

data class AlarmNotificationEntity(
    val timeAlarm : String,
    val idTopic : Long,
    val idContent : Long,
    val nameContent : String,
    val nameWork : String,
)