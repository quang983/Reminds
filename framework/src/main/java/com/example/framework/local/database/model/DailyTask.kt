package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.model.daily.DailyTaskEntity
import com.example.framework.local.database.convert.BaseConverter

@Entity
data class DailyTask(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var createTime: Long,
    var endTime: Long?,
    var remainingTime: Long?,
    var listDayOfWeek: String?
) : BaseConverter<DailyTask, DailyTaskEntity> {
    override fun convert(): DailyTaskEntity = DailyTaskEntity(id, name, createTime, endTime, remainingTime, listDayOfWeek)

    override fun copy(data: DailyTaskEntity): DailyTask {
        id = data.id
        name = data.name
        createTime = data.createTime
        endTime = data.endTime
        remainingTime = data.remainingTime
        listDayOfWeek = data.listDayOfWeek ?: ""
        return this
    }
}
