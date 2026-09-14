package com.ma_fantatra.domain.repository

import com.ma_fantatra.domain.model.Commune
import kotlinx.coroutines.flow.Flow

interface CommuneRepository {
    fun observeAll(): Flow<List<Commune>>
    suspend fun refresh()
}
