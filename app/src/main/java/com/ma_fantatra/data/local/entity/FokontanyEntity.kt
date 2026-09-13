package com.ma_fantatra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fokontany")
data class FokontanyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val communeName: String,
    val districtName: String,
    val regionName: String,
    val latitude: Double?,
    val longitude: Double?,
    val addressNote: String?,
    val openingHours: String?,
)