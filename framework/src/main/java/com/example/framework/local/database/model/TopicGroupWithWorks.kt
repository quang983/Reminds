package com.example.framework.local.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class TopicGroupWithWorks(
    @Embedded val topicGroup: TopicGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
    )
    val listWork: List<WorkFoTopic>
)

data class WorkWithContent(
    @Embedded val workGroup: WorkFoTopic,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
    )
    val list: List<ContentFoWork>
)