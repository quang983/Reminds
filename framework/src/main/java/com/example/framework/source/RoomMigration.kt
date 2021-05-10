package com.example.framework.source

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN doneAll INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN isShowContents INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE TopicGroup ADD COLUMN optionSelected INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN hashTag INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN timerReminder INTEGER DEFAULT 0 NOT NULL")
    }
}

//2.2 golive
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TopicGroup ADD COLUMN typeTopic INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN createTime INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN stt INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN description STRING NOT NULL")
        database.execSQL("ALTER TABLE TopicGroup ADD COLUMN imageSource INTEGER DEFAULT NULL")
    }
}

//2.5 golive
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE WorkFoTopic ADD COLUMN timerFocus INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE `DailyTask` (`id` BIGINT, `name` TEXT, `createTime` BIGINT,`endTime` BIGINT " +
                    "PRIMARY KEY(`id`))"
        )
        database.execSQL(
            "CREATE TABLE `DailyDivideTaskDone` (`id` BIGINT,`idGroup` BIGINT, `name` TEXT, `doneTime` BIGINT,`remainingTime` BIGINT " +
                    "PRIMARY KEY(`id`))"
        )
    }
}
