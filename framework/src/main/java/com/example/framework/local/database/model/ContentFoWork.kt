package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContentFoWork(
    @PrimaryKey(autoGenerate = true)
    var idContent: Long = 0,
    var name: String = "",
    var idOwnerWork: Long = 0,
    var hashTag: Boolean = false,
    var timer: Long = -1,
    var isCheckDone: Boolean = false
)
