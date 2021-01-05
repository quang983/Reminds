package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.BaseDataMapper
import com.example.common.base.model.WorkDataEntity

@Entity
class WorkFoTopic(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var idOwnerGroup: Long
) : BaseDataMapper<WorkFoTopic, WorkDataEntity> {
    override fun toDomain(model: WorkFoTopic) = WorkDataEntity(id, name, idOwnerGroup)

    override fun toData(entity: WorkDataEntity) = this

}
