package com.example.framework.local.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class TopicGroupWithWorks(
    @Embedded val topicGroup: TopicGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "idOwnerGroup",
    )
    val listWork: List<WorkFoTopic>
)

data class WorkWithContent(
    @Embedded val workGroup: WorkFoTopic,
    @Relation(
        parentColumn = "id",
        entityColumn = "idOwnerWork",
    )
    val list: List<ContentFoWork>
)