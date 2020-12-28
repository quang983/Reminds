package com.example.data.local.model

import com.example.domain.model.TopicGroupEntity
import javax.inject.Inject
import com.example.common.base.BaseDataMapper

class TopicDataGroupMapper @Inject constructor() :
    BaseDataMapper<TopicDataModel, TopicGroupEntity> {
    override fun toModel(entity: TopicGroupEntity): TopicDataModel {
        return TopicDataModel(
            id = entity.id,
            name = entity.name
        )
    }

    override fun toEntity(model: TopicDataModel): TopicGroupEntity {
        return TopicGroupEntity(
            id = model.id, name = model.name
        )
    }
}
