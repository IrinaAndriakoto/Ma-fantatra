package com.ma_fantatra.data.remote

import com.ma_fantatra.data.remote.dto.FokontanyDto
import com.ma_fantatra.data.remote.dto.CommuneDto
import com.ma_fantatra.data.remote.dto.ProcedureDetailDto
import com.ma_fantatra.data.remote.dto.ProcedureDto
import com.ma_fantatra.data.remote.dto.SyncResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface MafantatraApi {

    @GET("api/procedures")
    suspend fun getProcedures(): List<ProcedureDto>

    @GET("api/procedures/{id}")
    suspend fun getProcedure(@Path("id") id: Long): ProcedureDetailDto

    @GET("api/fokontany")
    suspend fun getFokontany(): List<FokontanyDto>

    @GET("api/communes")
    suspend fun getCommunes(): List<CommuneDto>

    @GET("api/sync")
    suspend fun sync(): SyncResponseDto
}
