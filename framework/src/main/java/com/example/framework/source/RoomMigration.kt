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

//chua golive
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `DailyTask` (`id` INTEGER DEFAULT 0 NOT NULL, `name` TEXT DEFAULT '' NOT NULL, `createTime` INTEGER DEFAULT 0 NOT NULL,`endTime` INTEGER DEFAULT 0 NOT NULL, PRIMARY KEY (`id`))")

        database.execSQL("CREATE TABLE IF NOT EXISTS `DailyDivideTaskDone` (`id` INTEGER DEFAULT 0 NOT NULL,`idGroup` INTEGER DEFAULT 0 NOT NULL, `name` TEXT DEFAULT '' NOT NULL, `doneTime` INTEGER DEFAULT 0 NOT NULL,`remainingTime` INTEGER DEFAULT 0 NOT NULL, PRIMARY KEY (`id`),FOREIGN KEY (`idGroup`) REFERENCES `DailyTask`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
    }
}
