package com.ma_fantatra.domain.repository

import com.ma_fantatra.domain.model.Fokontany
import kotlinx.coroutines.flow.Flow

interface FokontanyRepository {
    fun observeAll(): Flow<List<Fokontany>>
    fun observeById(id: Long): Flow<Fokontany?>
    suspend fun refresh()
}