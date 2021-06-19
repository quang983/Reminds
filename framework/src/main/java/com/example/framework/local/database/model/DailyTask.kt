package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.common.base.model.daily.DailyTaskEntity
import com.example.framework.local.database.convert.BaseConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


@Entity
data class DailyTask(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var desc: String?,
    var createTime: Long,
    var endTime: Long?,
    var remainingTime: Long?,
    var listDayOfWeek: String
) : BaseConverter<DailyTask, DailyTaskEntity> {

    override fun convert(): DailyTaskEntity {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<Int>>() {}.type
        val listDayOfWeekStr: ArrayList<Int> = gson.fromJson(listDayOfWeek, listType)
        return DailyTaskEntity(id, name, desc, createTime, endTime, remainingTime, listDayOfWeekStr)
    }

    override fun copy(data: DailyTaskEntity): DailyTask {
        val gson = Gson()
        id = data.id
        name = data.name
        createTime = data.createTime
        endTime = data.endTime
        remainingTime = data.remainingTime
        listDayOfWeek = gson.toJson(data.listDayOfWeek)
        return this
    }
}
