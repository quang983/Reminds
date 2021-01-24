package com.example.common.base.model

data class WorkDataEntity(
    var id: Long,
    val name: String,
    val groupId: Long,
    var listContent: MutableList<ContentDataEntity>
) {
    fun copy() = WorkDataEntity(
        id, name, groupId, listContent.map { it.copy() }.sortedWith(
            compareBy({ it.isCheckDone }, { !it.hashTag }, { it.id })
        ).toMutableList()
    )

    fun copyAndRemoveDone() = WorkDataEntity(
        id, name, groupId, listContent.map { it.copy() }
            .filter { !it.isCheckDone }.sortedWith(
                compareBy({ !it.hashTag }, { it.id })
            ).toMutableList()
    )

    fun copyAndClearFocus() = WorkDataEntity(
        id, name, groupId,
        listContent.filter { it.name.isNotEmpty() }.map { it.copy() }.toMutableList()
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