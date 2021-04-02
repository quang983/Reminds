package com.example.framework.source

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN doneAll INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN isShowContents INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE TopicGroup ADD COLUMN optionSelected INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN hashTag INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN timerReminder LONG DEFAULT 0 NOT NULL")
    }
}

//2.2 golive
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TopicGroup ADD COLUMN typeTopic INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN createTime LONG DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN stt INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN description STRING NOT NULL")
        database.execSQL("ALTER TABLE TopicGroup ADD COLUMN imageSource INTEGER")
    }
}