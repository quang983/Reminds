package com.example.common.base.model

data class TopicGroupEntity(
    val id: Long, val name: String, var isShowDone: Boolean = false
)