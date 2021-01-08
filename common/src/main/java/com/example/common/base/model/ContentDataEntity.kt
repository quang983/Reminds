package com.example.common.base.model

import androidx.room.Entity

@Entity
data class ContentDataEntity(var id: Long, var name: String, var idOwnerWork: Long, var isChecked: Boolean = false){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContentDataEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}