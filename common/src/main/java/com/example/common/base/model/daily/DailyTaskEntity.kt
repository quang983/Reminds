package com.example.common.base.model.daily

data class DailyTaskEntity(
    var id: Long,
    var name: String,
    var desc: String? = null,
    var createTime: Long,
    var endTime: Long? = null,
    var remainingTime: Long? = null,
    var listDayOfWeek: String? = null,
    var type: Int = 0
) {
    companion object {

    }
}