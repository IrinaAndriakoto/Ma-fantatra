package com.ma_fantatra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "procedure")
data class ProcedureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val cost: String,
    val processingTime: String,
    val description: String,
    val instructions: String,
)