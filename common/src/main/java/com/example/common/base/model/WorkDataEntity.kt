package com.example.common.base.model

data class WorkDataEntity(
    var id: Long,
    var name: String,
    val groupId: Long,
    var listContent: MutableList<ContentDataEntity>,
    var doneAll: Boolean,
    var isShowContents: Boolean = false,
    var hashTag: Boolean = false,
    var timerReminder: Long = -1,
    var createTime: Long = id,
    var stt: Int = STT_WITHOUT_DB,
    var description: String = "",
    var timerFocus: Long = -1

) {
    fun copy() = WorkDataEntity(
        id, name, groupId, listContent.map { it.copy() }.toMutableList(),
        doneAll = doneAll, isShowContents = isShowContents, hashTag,
        timerReminder, createTime, stt, description, timerFocus
    )

    fun copySort() = WorkDataEntity(
        id, name, groupId,
        listContent
            .map { it.copy() }
            .sortedWith(compareBy({ it.isCheckDone }, { !it.hashTag }, { it.id }))
            .toMutableList(), doneAll = doneAll && listContent.all { it.isCheckDone },
        isShowContents = isShowContents, hashTag, timerReminder, createTime, stt, description, timerFocus
    )

    fun copyFilterNotEmpty() = WorkDataEntity(
        id, name, groupId,
        listContent
            .filter { it.name.isNotEmpty() }
            .map { it.copy() }.toMutableList(),
        doneAll = doneAll && listContent.all { it.isCheckDone },
        isShowContents = isShowContents, hashTag, timerReminder, createTime, stt, description, timerFocus
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

    companion object {
        const val STT_WITHOUT_DB = -1
    }
}