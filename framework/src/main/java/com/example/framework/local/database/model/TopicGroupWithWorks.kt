package com.example.framework.local.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class TopicGroupWithWorks(
    @Embedded val topicGroup: TopicGroup,
    @Relation(
        entity = WorkFoTopic::class,
        parentColumn = "idTopic",
        entityColumn = "idOwnerGroup"
    )
    val listWork: List<WorkFoTopic>
)
