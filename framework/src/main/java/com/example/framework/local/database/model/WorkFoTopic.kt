package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.BaseDataMapper
import com.example.common.base.model.WorkDataEntity

@Entity
class WorkFoTopic : BaseDataMapper<WorkFoTopic, WorkDataEntity> {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String = ""

    override fun toEntity(model: WorkFoTopic) = WorkDataEntity(id, name)

    override fun toModel(entity: WorkDataEntity) = this

}
