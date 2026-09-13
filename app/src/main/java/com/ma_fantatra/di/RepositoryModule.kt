package com.ma_fantatra.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.local.dao.ProcedureDao
import com.ma_fantatra.data.repository.DataStoreChecklistRepository
import com.ma_fantatra.data.repository.RoomFokontanyRepository
import com.ma_fantatra.data.repository.RoomProcedureRepository
import com.ma_fantatra.domain.repository.ChecklistRepository
import com.ma_fantatra.domain.repository.FokontanyRepository
import com.ma_fantatra.domain.repository.ProcedureRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProcedureRepository(procedureDao: ProcedureDao): ProcedureRepository =
        RoomProcedureRepository(procedureDao)

    @Provides
    @Singleton
    fun provideFokontanyRepository(fokontanyDao: FokontanyDao): FokontanyRepository =
        RoomFokontanyRepository(fokontanyDao)

    @Provides
    @Singleton
    fun provideChecklistRepository(dataStore: DataStore<Preferences>): ChecklistRepository =
        DataStoreChecklistRepository(dataStore)
}