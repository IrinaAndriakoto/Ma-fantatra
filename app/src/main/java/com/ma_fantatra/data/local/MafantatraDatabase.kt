package com.ma_fantatra.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.local.dao.ProcedureDao
import com.ma_fantatra.data.local.entity.DocumentRequirementEntity
import com.ma_fantatra.data.local.entity.FokontanyEntity
import com.ma_fantatra.data.local.entity.ProcedureEntity

@Database(
    entities = [
        ProcedureEntity::class,
        DocumentRequirementEntity::class,
        FokontanyEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class MafantatraDatabase : RoomDatabase() {
    abstract fun procedureDao(): ProcedureDao
    abstract fun fokontanyDao(): FokontanyDao

    companion object {
        const val DATABASE_NAME = "ma_fantatra.db"
        const val ASSET_DATABASE = "databases/ma_fantatra.db"
    }
}