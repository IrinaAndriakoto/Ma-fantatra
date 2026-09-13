package com.ma_fantatra.domain.repository

import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {
    fun observeCheckedDocumentIds(): Flow<Set<Long>>
    suspend fun setChecked(documentId: Long, checked: Boolean)
}