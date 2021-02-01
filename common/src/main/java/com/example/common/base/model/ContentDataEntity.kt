package com.example.common.base.model

import java.io.Serializable


data class ContentDataEntity(
    var id: Long,
    var name: String,
    var idOwnerWork: Long,
    var isFocus: Boolean = false,
    var hashTag: Boolean = false,
    var timer: Long = -1,
    var isCheckDone: Boolean = false
) : Serializable {
    fun copy() = ContentDataEntity(id, name, idOwnerWork, isFocus, hashTag, timer, isCheckDone)

    fun copyAndResetFocus() = ContentDataEntity(id, name, idOwnerWork, false, hashTag, timer, isCheckDone)


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
