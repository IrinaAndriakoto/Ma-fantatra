package com.ma_fantatra.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ProceduresRoute

@Serializable
data object DirectoryRoute

@Serializable
data object CommunesRoute

@Serializable
data class ProcedureDetailRoute(val procedureId: Long)

@Serializable
data class CommuneDetailRoute(val communeId: Long)

@Serializable
data class FokontanyDetailRoute(val fokontanyId: Long)