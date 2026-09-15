package com.ma_fantatra.data.repository

import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.mapper.toDomain
import com.ma_fantatra.data.mapper.toEntity
import com.ma_fantatra.data.remote.MafantatraApi
import com.ma_fantatra.domain.model.Fokontany
import com.ma_fantatra.domain.repository.FokontanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomFokontanyRepository @Inject constructor(
    private val fokontanyDao: FokontanyDao,
    private val api: MafantatraApi,
) : FokontanyRepository {

    override fun observeAll(): Flow<List<Fokontany>> =
        fokontanyDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Fokontany?> =
        fokontanyDao.observeById(id).map { it?.toDomain() }

    override suspend fun refresh() {
        try {
            val fokontany = api.getFokontany()
            fokontanyDao.upsertAll(fokontany.map { it.toEntity() })
        } catch (_: Exception) {
            // Offline — keep existing cache
        }
    }
}
