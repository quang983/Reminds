package com.example.framework.model

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
        return this
    }

    override fun toEntity(model: TopicGroup): TopicDataModel {
        return TopicDataModel(
            id = model.id, name = model.name
        )
    }
}