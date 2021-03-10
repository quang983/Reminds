package com.example.common.base.model

data class WorkDataEntity(
    var id: Long,
    val name: String,
    val groupId: Long,
    var listContent: MutableList<ContentDataEntity>,
    var doneAll: Boolean,
    var isShowContents: Boolean = false
) {
    fun copy() = WorkDataEntity(
        id, name, groupId, listContent.map { it.copy() }.toMutableList(),
        doneAll = doneAll, isShowContents = isShowContents
    )

    fun copySort() = WorkDataEntity(
        id, name, groupId, listContent.map { it.copy() }.sortedWith(
            compareBy({ it.isCheckDone }, { !it.hashTag }, { it.id })
        ).toMutableList(), doneAll = doneAll && listContent.all { it.isCheckDone }, isShowContents = isShowContents
    )

    fun copyState() = WorkDataEntity(
        id,
        name,
        groupId,
        listContent.filter {
            it.name.isNotEmpty()
        }.map {
            it.copy()
        }.toMutableList(),
        doneAll = doneAll && listContent.all { it.isCheckDone }, isShowContents = isShowContents
    )

    fun copyAndResetFocus() = WorkDataEntity(
        id, name, groupId,
        listContent.filter {
            it.name.isNotEmpty()
        }.map {
            it.copyAndResetFocus()
        }.toMutableList(), doneAll = doneAll
                && listContent.all { it.isCheckDone }, isShowContents = isShowContents
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