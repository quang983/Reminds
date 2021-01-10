package com.example.common.base.model

import androidx.room.Entity

@Entity
data class ContentDataEntity(
    var id: Long, var name: String,
    var idOwnerWork: Long, var isChecked: Boolean,
    var isFocus: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContentDataEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (idOwnerWork != other.idOwnerWork) return false
        if (isChecked != other.isChecked) return false
        if (isFocus != other.isFocus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + idOwnerWork.hashCode()
        result = 31 * result + isChecked.hashCode()
        result = 31 * result + isFocus.hashCode()
        return result
    }
}