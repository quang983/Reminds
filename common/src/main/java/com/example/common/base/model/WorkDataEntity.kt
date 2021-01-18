package com.example.common.base.model

import androidx.room.Entity

@Entity
data class WorkDataEntity(
    var id: Long,
    val name: String,
    val groupId: Long,
    var listContent: MutableList<ContentDataEntity>,
    var listContentDone: MutableList<ContentDataEntity>
) {
    fun copy() = WorkDataEntity(
        id, name, groupId, listContent.map { it.copy() }.toMutableList(), listContentDone.map { it.copy() }.toMutableList()
    )

    fun copyAndClearFocus() = WorkDataEntity(
        id, name, groupId,
        listContent.filter { it.name.isNotEmpty() }.map { it.copy() } as MutableList<ContentDataEntity>,
        listContentDone.map { it.copy() } as MutableList<ContentDataEntity>
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorkDataEntity

        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}