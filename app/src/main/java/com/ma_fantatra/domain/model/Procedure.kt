package com.ma_fantatra.domain.model

data class Procedure(
    val id: Long,
    val title: String,
    val category: ProcedureCategory,
    val cost: String,
    val processingTime: String,
    val description: String,
    val instructions: String,
)

data class DocumentRequirement(
    val id: Long,
    val procedureId: Long,
    val title: String,
    val isMandatory: Boolean,
    val note: String?,
)

data class Fokontany(
    val id: Long,
    val name: String,
    val communeName: String,
    val districtName: String,
    val regionName: String,
    val latitude: Double?,
    val longitude: Double?,
    val addressNote: String?,
    val openingHours: String?,
)