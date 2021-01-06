package com.example.framework.local.database.dao

import androidx.room.Dao
import com.example.framework.local.database.base.BaseDao
import com.example.framework.local.database.model.WorkFoTopic

@Dao
interface LocalWorkWithChildDao : BaseDao<WorkFoTopic>