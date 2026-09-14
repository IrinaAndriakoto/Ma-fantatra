package com.ma_fantatra.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ma_fantatra.data.local.dao.CommuneDao
import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.local.dao.ProcedureDao
import com.ma_fantatra.data.local.entity.CommuneEntity
import com.ma_fantatra.data.local.entity.DocumentRequirementEntity
import com.ma_fantatra.data.local.entity.FokontanyEntity
import com.ma_fantatra.data.local.entity.ProcedureEntity

@Database(
    entities = [
        ProcedureEntity::class,
        DocumentRequirementEntity::class,
        FokontanyEntity::class,
        CommuneEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class MafantatraDatabase : RoomDatabase() {
    abstract fun procedureDao(): ProcedureDao
    abstract fun fokontanyDao(): FokontanyDao
    abstract fun communeDao(): CommuneDao

    companion object {
        const val DATABASE_NAME = "ma_fantatra.db"
        const val ASSET_DATABASE = "databases/ma_fantatra.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `commune` (
                        `id` INTEGER PRIMARY KEY NOT NULL,
                        `name` TEXT NOT NULL,
                        `districtName` TEXT NOT NULL,
                        `regionName` TEXT NOT NULL,
                        `latitude` REAL,
                        `longitude` REAL
                    )"""
                )
            }
        }
    }
}
