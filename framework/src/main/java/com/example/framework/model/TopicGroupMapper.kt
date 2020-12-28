package com.example.framework.model

import com.example.common.base.BaseDataMapper
import com.example.data.local.model.TopicDataModel
import javax.inject.Inject

class TopicGroupMapper @Inject constructor() : BaseDataMapper<TopicGroup, TopicDataModel> {
    override fun toModel(entity: TopicDataModel): TopicGroup {
        return TopicGroup(
            id = entity.id,
            name = entity.name
        )
    }

    override fun toEntity(model: TopicGroup): TopicDataModel {
        return TopicDataModel(
            id = model.id, name = model.name
        )
    }
}
