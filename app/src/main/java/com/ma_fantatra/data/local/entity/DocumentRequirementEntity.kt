package com.ma_fantatra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "document_requirement")
data class DocumentRequirementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val procedureId: Long,
    val title: String,
    val isMandatory: Boolean,
    val note: String?,
)