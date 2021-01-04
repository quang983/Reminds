package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.BaseDataMapper
import com.example.data.local.model.TopicDataModel

@Entity
class TopicGroup : BaseDataMapper<TopicGroup, TopicDataModel> {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String = ""

    override fun toModel(entity: TopicDataModel): TopicGroup {
        val topicGroup = TopicGroup()
        topicGroup.id = entity.id
        topicGroup.name = entity.name
        return topicGroup
    }

    override fun toEntity(model: TopicGroup): TopicDataModel {
        return TopicDataModel(
            id = model.id, name = model.name
        )
    }
}