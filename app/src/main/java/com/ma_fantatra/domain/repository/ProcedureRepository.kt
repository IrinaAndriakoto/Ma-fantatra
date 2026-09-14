package com.ma_fantatra.domain.repository

import com.ma_fantatra.domain.model.DocumentRequirement
import com.ma_fantatra.domain.model.Procedure
import kotlinx.coroutines.flow.Flow

interface ProcedureRepository {
    fun observeAll(): Flow<List<Procedure>>
    fun observeById(id: Long): Flow<Procedure?>
    fun observeRequirements(procedureId: Long): Flow<List<DocumentRequirement>>
    suspend fun refresh()
}