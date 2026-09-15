package com.ma_fantatra.data.repository

import com.ma_fantatra.data.local.dao.CommuneDao
import com.ma_fantatra.data.mapper.toDomain
import com.ma_fantatra.data.mapper.toEntity
import com.ma_fantatra.data.remote.MafantatraApi
import com.ma_fantatra.domain.model.Commune
import com.ma_fantatra.domain.repository.CommuneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomCommuneRepository @Inject constructor(
    private val communeDao: CommuneDao,
    private val api: MafantatraApi,
) : CommuneRepository {

    override fun observeAll(): Flow<List<Commune>> =
        communeDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Commune?> =
        communeDao.observeById(id).map { it?.toDomain() }

    override suspend fun refresh() {
        try {
            val communes = api.getCommunes()
            communeDao.upsertAll(communes.map { it.toEntity() })
        } catch (_: Exception) {
            // Offline — keep existing cache
        }
    }
}
