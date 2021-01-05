package com.example.common.base.model

import androidx.room.Entity

@Entity
data class TopicGroupWithWorksEntity(val topic: TopicDataModel, val works: List<WorkDataEntity>)