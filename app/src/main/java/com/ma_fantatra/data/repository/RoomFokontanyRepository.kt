package com.ma_fantatra.data.repository

import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.mapper.toDomain
import com.ma_fantatra.domain.model.Fokontany
import com.ma_fantatra.domain.repository.FokontanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFokontanyRepository(
    private val fokontanyDao: FokontanyDao,
) : FokontanyRepository {

    override fun observeAll(): Flow<List<Fokontany>> =
        fokontanyDao.observeAll().map { entities -> entities.map { it.toDomain() } }
}