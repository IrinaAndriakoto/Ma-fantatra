package com.ma_fantatra.data.repository

import com.ma_fantatra.data.local.dao.ProcedureDao
import com.ma_fantatra.data.mapper.toDomain
import com.ma_fantatra.domain.model.DocumentRequirement
import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.domain.repository.ProcedureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomProcedureRepository(
    private val procedureDao: ProcedureDao,
) : ProcedureRepository {

    override fun observeAll(): Flow<List<Procedure>> =
        procedureDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Procedure?> =
        procedureDao.observeById(id).map { it?.toDomain() }

    override fun observeRequirements(procedureId: Long): Flow<List<DocumentRequirement>> =
        procedureDao.observeRequirements(procedureId).map { entities -> entities.map { it.toDomain() } }
}