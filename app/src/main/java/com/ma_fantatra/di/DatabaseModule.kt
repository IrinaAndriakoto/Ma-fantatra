package com.ma_fantatra.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import com.ma_fantatra.data.local.MafantatraDatabase
import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.local.dao.ProcedureDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MafantatraDatabase =
        Room.databaseBuilder(context, MafantatraDatabase::class.java, MafantatraDatabase.DATABASE_NAME)
            .createFromAsset(MafantatraDatabase.ASSET_DATABASE)
            .build()

    @Provides
    fun provideProcedureDao(database: MafantatraDatabase): ProcedureDao = database.procedureDao()

    @Provides
    fun provideFokontanyDao(database: MafantatraDatabase): FokontanyDao = database.fokontanyDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("user_settings") }
}