package com.ma_fantatra.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ma_fantatra.data.local.dao.CommuneDao
import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.local.dao.ProcedureDao
import com.ma_fantatra.data.remote.MafantatraApi
import com.ma_fantatra.data.repository.DataStoreChecklistRepository
import com.ma_fantatra.data.repository.RoomCommuneRepository
import com.ma_fantatra.data.repository.RoomFokontanyRepository
import com.ma_fantatra.data.repository.RoomProcedureRepository
import com.ma_fantatra.domain.repository.ChecklistRepository
import com.ma_fantatra.domain.repository.CommuneRepository
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
    fun provideProcedureRepository(
        procedureDao: ProcedureDao,
        api: MafantatraApi,
    ): ProcedureRepository = RoomProcedureRepository(procedureDao, api)

    @Provides
    @Singleton
    fun provideFokontanyRepository(
        fokontanyDao: FokontanyDao,
        api: MafantatraApi,
    ): FokontanyRepository = RoomFokontanyRepository(fokontanyDao, api)

    @Provides
    @Singleton
    fun provideCommuneRepository(
        communeDao: CommuneDao,
        api: MafantatraApi,
    ): CommuneRepository = RoomCommuneRepository(communeDao, api)

    @Provides
    @Singleton
    fun provideChecklistRepository(dataStore: DataStore<Preferences>): ChecklistRepository =
        DataStoreChecklistRepository(dataStore)
}
