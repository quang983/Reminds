package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.framework.local.database.convert.DataConverter

@Entity
class WorkFoTopic(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val idOwnerGroup: Long,
    @TypeConverters(DataConverter::class)
    val listContent: MutableList<ContentFoWork>,
    val doneAll: Boolean = false,
    val isShowContents: Boolean = false,
    var hashTag: Boolean = false,
    var timerReminder: Long = -1,
    var createTime : Long = id,
    var stt : Int
)
