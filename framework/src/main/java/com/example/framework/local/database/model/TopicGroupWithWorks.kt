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
    val listWork: List<WorkWithContent>
)

data class WorkWithContent(
    @Embedded val workGroup: WorkFoTopic,
    @Relation(
        parentColumn = "id",
        entityColumn = "idOwnerWork",
    )
    val listContent: List<ContentFoWork>
)