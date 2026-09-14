package com.ma_fantatra.data.mapper

import com.ma_fantatra.data.local.entity.CommuneEntity
import com.ma_fantatra.data.local.entity.DocumentRequirementEntity
import com.ma_fantatra.data.local.entity.FokontanyEntity
import com.ma_fantatra.data.local.entity.ProcedureEntity
import com.ma_fantatra.data.remote.dto.CommuneDto
import com.ma_fantatra.data.remote.dto.DocumentRequirementDto
import com.ma_fantatra.data.remote.dto.FokontanyDto
import com.ma_fantatra.data.remote.dto.ProcedureDto

fun ProcedureDto.toEntity(): ProcedureEntity = ProcedureEntity(
    id = id,
    title = title,
    category = category,
    cost = cost,
    processingTime = processingTime,
    description = description,
    instructions = instructions,
)

fun DocumentRequirementDto.toEntity(procedureId: Long): DocumentRequirementEntity = DocumentRequirementEntity(
    id = id,
    procedureId = procedureId,
    title = title,
    isMandatory = isMandatory,
    note = note,
)

fun FokontanyDto.toEntity(): FokontanyEntity = FokontanyEntity(
    id = id,
    name = name,
    communeName = communeName,
    districtName = districtName,
    regionName = regionName,
    latitude = latitude,
    longitude = longitude,
    addressNote = addressNote,
    openingHours = openingHours,
)

fun CommuneDto.toEntity(): CommuneEntity = CommuneEntity(
    id = id,
    name = name,
    districtName = districtName,
    regionName = regionName,
    latitude = latitude,
    longitude = longitude,
)
