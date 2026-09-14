package com.ma_fantatra.data.repository

import com.ma_fantatra.data.local.dao.ProcedureDao
import com.ma_fantatra.data.mapper.toDomain
import com.ma_fantatra.data.mapper.toEntity
import com.ma_fantatra.data.remote.MafantatraApi
import com.ma_fantatra.domain.model.DocumentRequirement
import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.domain.repository.ProcedureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomProcedureRepository @Inject constructor(
    private val procedureDao: ProcedureDao,
    private val api: MafantatraApi,
) : ProcedureRepository {

    override fun observeAll(): Flow<List<Procedure>> =
        procedureDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Procedure?> =
        procedureDao.observeById(id).map { it?.toDomain() }

    override fun observeRequirements(procedureId: Long): Flow<List<DocumentRequirement>> =
        procedureDao.observeRequirements(procedureId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun refresh() {
        try {
            val procedures = api.getProcedures()
            procedureDao.deleteAll()
            procedureDao.deleteAllRequirements()
            procedureDao.upsertAll(procedures.map { it.toEntity() })
        } catch (_: Exception) {
            // Offline — keep existing cache
        }
    }
}
