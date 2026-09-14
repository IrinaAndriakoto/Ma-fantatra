package com.ma_fantatra.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProcedureDto(
    val id: Long,
    val title: String,
    val category: String,
    val cost: String,
    val processingTime: String,
    val description: String,
    val instructions: String,
)

@Serializable
data class DocumentRequirementDto(
    val id: Long,
    val title: String,
    val isMandatory: Boolean,
    val note: String? = null,
)

@Serializable
data class ProcedureDetailDto(
    val id: Long,
    val title: String,
    val category: String,
    val cost: String,
    val processingTime: String,
    val description: String,
    val instructions: String,
    val requirements: List<DocumentRequirementDto>,
)

@Serializable
data class FokontanyDto(
    val id: Long,
    val name: String,
    val communeName: String,
    val districtName: String,
    val regionName: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val addressNote: String? = null,
    val openingHours: String? = null,
)

@Serializable
data class CommuneDto(
    val id: Long,
    val name: String,
    val districtName: String,
    val regionName: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class SyncResponseDto(
    val procedures: List<ProcedureDto>,
    val fokontany: List<FokontanyDto>,
    val communes: List<CommuneDto>,
    val lastSync: String,
)
