package com.ma_fantatra.domain.model

enum class ProcedureCategory(val storageKey: String, val label: String) {
    ETAT_CIVIL("État Civil", "État civil"),
    CARTES_IDENTITE("Cartes & Identité", "Cartes & Identité"),
    ATTESTATIONS("Attestations", "Attestations"),
    DIVERS("Divers", "Divers");

    companion object {
        fun fromStorage(value: String): ProcedureCategory =
            entries.firstOrNull { it.storageKey == value } ?: DIVERS
    }
}