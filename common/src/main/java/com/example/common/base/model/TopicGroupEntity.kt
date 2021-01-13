package com.example.common.base.model

data class TopicGroupEntity(
    val id: Long, val name: String, var startDate: Long = 0,
    var endDate: Long? = null
)