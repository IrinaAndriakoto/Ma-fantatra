package com.ma_fantatra.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ma_fantatra.data.local.entity.FokontanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FokontanyDao {
    @Query("SELECT * FROM fokontany ORDER BY regionName, districtName, communeName, name COLLATE NOCASE")
    fun observeAll(): Flow<List<FokontanyEntity>>

    @Upsert
    suspend fun upsertAll(fokontany: List<FokontanyEntity>)
}