package com.example.common.base.model

data class WorkDataEntity(
    var id: Long,
    var name: String,
    val groupId: Long,
    var listContent: MutableList<ContentDataEntity>,
    var doneAll: Boolean,
    var isShowContents: Boolean = false,
    var hashTag: Boolean = false,
    var timerReminder: Long = -1
) {
    fun copy() = WorkDataEntity(
        id, name, groupId, listContent.map { it.copy() }.toMutableList(),
        doneAll = doneAll, isShowContents = isShowContents, hashTag, timerReminder
    )

    fun copySort() = WorkDataEntity(
        id, name, groupId,
        listContent
            .map { it.copy() }
            .sortedWith(compareBy({ it.isCheckDone }, { !it.hashTag }, { it.id }))
            .toMutableList(), doneAll = doneAll && listContent.all { it.isCheckDone },
        isShowContents = isShowContents, hashTag, timerReminder
    )

    fun copyFilterNotEmpty() = WorkDataEntity(
        id, name, groupId,
        listContent
            .filter { it.name.isNotEmpty() }
            .map { it.copy() }.toMutableList(),
        doneAll = doneAll && listContent.all { it.isCheckDone }, isShowContents = isShowContents, hashTag, timerReminder
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