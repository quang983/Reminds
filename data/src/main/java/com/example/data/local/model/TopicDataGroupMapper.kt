package com.example.data.local.model

import com.example.common.base.model.TopicGroupEntity
import javax.inject.Inject
import com.example.common.base.BaseDataMapper
import com.example.common.base.model.TopicDataModel

class TopicDataGroupMapper @Inject constructor() :
    BaseDataMapper<TopicDataModel, TopicGroupEntity> {
    override fun toData(entity: TopicGroupEntity): TopicDataModel {
        return TopicDataModel(
            id = entity.id,
            name = entity.name
        )
    }

    override fun toDomain(model: TopicDataModel): TopicGroupEntity {
        return TopicGroupEntity(
            id = model.id, name = model.name
        )
    }
}
