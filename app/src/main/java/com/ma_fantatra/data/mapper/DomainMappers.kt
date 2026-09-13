package com.ma_fantatra.data.mapper

import com.ma_fantatra.data.local.entity.DocumentRequirementEntity
import com.ma_fantatra.data.local.entity.FokontanyEntity
import com.ma_fantatra.data.local.entity.ProcedureEntity
import com.ma_fantatra.domain.model.DocumentRequirement
import com.ma_fantatra.domain.model.Fokontany
import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.domain.model.ProcedureCategory

fun ProcedureEntity.toDomain(): Procedure = Procedure(
    id = id,
    title = title,
    category = ProcedureCategory.fromStorage(category),
    cost = cost,
    processingTime = processingTime,
    description = description,
    instructions = instructions,
)

fun DocumentRequirementEntity.toDomain(): DocumentRequirement = DocumentRequirement(
    id = id,
    procedureId = procedureId,
    title = title,
    isMandatory = isMandatory,
    note = note,
)

fun FokontanyEntity.toDomain(): Fokontany = Fokontany(
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