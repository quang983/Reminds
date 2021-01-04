package com.example.framework.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ContentFoWork {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String = ""
}
