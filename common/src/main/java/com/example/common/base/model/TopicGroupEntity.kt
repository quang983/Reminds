package com.example.common.base.model

data class TopicGroupEntity(
    val id: Long, val name: String, var isShowDone: Boolean = true, var optionSelected: Int = SHOW_ALL_WORKS
) {
    companion object {
        const val SHOW_ALL_WORKS = 0
        const val HIDE_DONE_WORKS = 1
        const val REMOVE_DONE_WORKS = 2
    }
}