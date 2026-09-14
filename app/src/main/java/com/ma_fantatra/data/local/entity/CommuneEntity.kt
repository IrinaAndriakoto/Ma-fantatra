package com.ma_fantatra.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commune")
data class CommuneEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val districtName: String,
    val regionName: String,
    val latitude: Double?,
    val longitude: Double?,
)
