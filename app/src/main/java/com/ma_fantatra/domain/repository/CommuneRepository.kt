package com.ma_fantatra.domain.repository

import com.ma_fantatra.domain.model.Commune
import kotlinx.coroutines.flow.Flow

interface CommuneRepository {
    fun observeAll(): Flow<List<Commune>>
    fun observeById(id: Long): Flow<Commune?>
    suspend fun refresh()
}
