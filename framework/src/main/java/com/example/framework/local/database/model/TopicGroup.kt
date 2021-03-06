package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.BaseDataMapper
import com.example.common.base.model.TopicGroupEntity

@Entity
data class TopicGroup(
    @PrimaryKey(autoGenerate = true)
    var idTopic: Long = 0,
    var name: String = "",
    var isShowDone: Boolean = true,
    var optionSelected: Int = TopicGroupEntity.SHOW_ALL_WORKS,
    var typeTopic: Int = TopicGroupEntity.TYPE_NORMAL,
    var imageSource: Int? = null
) : BaseDataMapper<TopicGroup, TopicGroupEntity> {

    override fun toData(entity: TopicGroupEntity): TopicGroup {
        val topicGroup = TopicGroup()
        topicGroup.idTopic = entity.id
        topicGroup.name = entity.name
        topicGroup.isShowDone = entity.isShowDone
        topicGroup.optionSelected = entity.optionSelected
        topicGroup.typeTopic = entity.typeTopic
        topicGroup.imageSource = entity.iconResource
        return topicGroup
    }

    override fun toDomain(model: TopicGroup): TopicGroupEntity {
        return TopicGroupEntity(
            id = model.idTopic, name = model.name,
            isShowDone = model.isShowDone, optionSelected = model.optionSelected,
            typeTopic = model.typeTopic, iconResource = model.imageSource
        )
    }
}