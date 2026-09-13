package com.ma_fantatra.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.ma_fantatra.data.local.entity.DocumentRequirementEntity
import com.ma_fantatra.data.local.entity.ProcedureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcedureDao {
    @Query("SELECT * FROM `procedure` ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<ProcedureEntity>>

    @Query("SELECT * FROM `procedure` WHERE id = :id")
    fun observeById(id: Long): Flow<ProcedureEntity?>

    @Query("SELECT * FROM document_requirement WHERE procedureId = :procedureId ORDER BY id")
    fun observeRequirements(procedureId: Long): Flow<List<DocumentRequirementEntity>>
}