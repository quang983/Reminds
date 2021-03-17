package com.example.common.base.model

data class TopicGroupEntity(
    val id: Long, val name: String, var isShowDone: Boolean = true,
    var optionSelected: Int = SHOW_ALL_WORKS, var typeTopic: Int = TYPE_NORMAL
) {
    companion object {
        const val SHOW_ALL_WORKS = 0
        const val HIDE_DONE_WORKS = 1
        const val REMOVE_DONE_WORKS = 2

        const val TYPE_NORMAL = 0
        const val TYPE_UPCOMING = 1
        const val TYPE_FAST = 2
    }
}