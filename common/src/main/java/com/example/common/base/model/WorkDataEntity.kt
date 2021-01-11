package com.example.common.base.model

import androidx.room.Entity

@Entity
data class WorkDataEntity(
    val id: Long,
    val name: String,
    val groupId: Long,
    var listContent: List<ContentDataEntity>
) {

    fun clone(): WorkDataEntity = WorkDataEntity(id, name, groupId, listContent.map { it.clone() })

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkDataEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        return result
    }
}