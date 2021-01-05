package com.example.framework.local.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class TopicGroupWithWorks(
    @Embedded val topicGroup: TopicGroup,
    @Relation(
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