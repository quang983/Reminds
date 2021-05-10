package com.example.framework.local.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.example.common.base.model.daily.DailyTaskWithDividerEntity
import com.example.framework.local.database.convert.BaseConverter

@Entity
data class DailyTaskWithDivider(
    @Embedded
    var topicGroup: DailyTask,
    @Relation(
        entity = DailyDivideTaskDone::class,
        parentColumn = "id",
        entityColumn = "idGroup"
    )
    var listDaily: ArrayList<DailyDivideTaskDone>
) : BaseConverter<DailyTaskWithDivider, DailyTaskWithDividerEntity> {
    override fun convert(): DailyTaskWithDividerEntity =
        DailyTaskWithDividerEntity(topicGroup.convert(), listDaily.map { it.convert() })

    override fun copy(data: DailyTaskWithDividerEntity): DailyTaskWithDivider {
        topicGroup = topicGroup.copy(data.dailyTask)
        listDaily.clear()
        listDaily.addAll(data.dailyList.map { DailyDivideTaskDone(it.id, it.idGroup, it.name, it.doneTime, it.remainingTime) })
        return this
    }
}
