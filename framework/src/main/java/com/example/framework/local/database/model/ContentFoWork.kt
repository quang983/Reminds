package com.example.framework.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ContentFoWork {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var idContent: Long = 0
    var name: String = ""
    var idOwnerWork : Long = 0
}
