package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.BaseDataMapper
import com.example.common.base.model.TopicDataModel
import com.example.common.base.model.TopicGroupEntity

@Entity
class TopicGroup : BaseDataMapper<TopicGroup, TopicGroupEntity> {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String = ""

    override fun toData(entity: TopicGroupEntity): TopicGroup {
        val topicGroup = TopicGroup()
        topicGroup.id = entity.id
        topicGroup.name = entity.name
        return topicGroup
    }

    override fun toDomain(model: TopicGroup): TopicGroupEntity {
        return TopicGroupEntity(
            id = model.id, name = model.name
        )
    }
}