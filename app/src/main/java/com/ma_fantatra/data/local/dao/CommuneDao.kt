package com.ma_fantatra.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ma_fantatra.data.local.entity.CommuneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommuneDao {
    @Query("SELECT * FROM commune ORDER BY regionName, districtName, name COLLATE NOCASE")
    fun observeAll(): Flow<List<CommuneEntity>>

    @Query("SELECT * FROM commune WHERE id = :id")
    fun observeById(id: Long): Flow<CommuneEntity?>

    @Upsert
    suspend fun upsertAll(communes: List<CommuneEntity>)
}
