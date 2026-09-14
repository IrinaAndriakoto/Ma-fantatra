package com.ma_fantatra.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.ma_fantatra.data.local.MafantatraDatabase
import com.ma_fantatra.data.local.dao.CommuneDao
import com.ma_fantatra.data.local.dao.FokontanyDao
import com.ma_fantatra.data.local.dao.ProcedureDao
import com.ma_fantatra.data.remote.MafantatraApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val BASE_URL = "http://10.0.2.2:8000/"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MafantatraDatabase =
        Room.databaseBuilder(context, MafantatraDatabase::class.java, MafantatraDatabase.DATABASE_NAME)
            .createFromAsset(MafantatraDatabase.ASSET_DATABASE)
            .addMigrations(MafantatraDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideProcedureDao(database: MafantatraDatabase): ProcedureDao = database.procedureDao()

    @Provides
    fun provideFokontanyDao(database: MafantatraDatabase): FokontanyDao = database.fokontanyDao()

    @Provides
    fun provideCommuneDao(database: MafantatraDatabase): CommuneDao = database.communeDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("user_settings") }

    @Provides
    @Singleton
    fun provideApi(): MafantatraApi {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(MafantatraApi::class.java)
    }
}
