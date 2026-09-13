package com.ma_fantatra.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ma_fantatra.domain.repository.ChecklistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreChecklistRepository(
    private val dataStore: DataStore<Preferences>,
) : ChecklistRepository {

    override fun observeCheckedDocumentIds(): Flow<Set<Long>> =
        dataStore.data.map { preferences ->
            preferences.asMap()
                .mapNotNull { (key, _) ->
                    val name = (key as? Preferences.Key<*>)?.name
                        ?: return@mapNotNull null
                    if (!name.startsWith(KEY_PREFIX)) return@mapNotNull null
                    val documentId = name.removePrefix(KEY_PREFIX).toLongOrNull()
                        ?: return@mapNotNull null
                    if (preferences[booleanPreferencesKey(name)] == true) documentId else null
                }
                .toSet()
        }

    override suspend fun setChecked(documentId: Long, checked: Boolean) {
        dataStore.edit { preferences ->
            val key = booleanPreferencesKey(KEY_PREFIX + documentId)
            if (checked) {
                preferences[key] = true
            } else {
                preferences.remove(key)
            }
        }
    }

    private companion object {
        const val KEY_PREFIX = "checked_doc_"
    }
}