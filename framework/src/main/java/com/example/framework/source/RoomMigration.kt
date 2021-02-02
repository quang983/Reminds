package com.example.framework.source

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomMigration {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
}