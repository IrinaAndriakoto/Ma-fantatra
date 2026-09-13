package com.ma_fantatra.testing

import com.ma_fantatra.domain.model.DocumentRequirement
import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.domain.repository.ProcedureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProcedureRepository(
    private val procedures: List<Procedure> = emptyList(),
    private val requirementsByProcedure: Map<Long, List<DocumentRequirement>> = emptyMap(),
) : ProcedureRepository {

    override fun observeAll(): Flow<List<Procedure>> = flowOf(procedures)

    override fun observeById(id: Long): Flow<Procedure?> =
        flowOf(procedures.firstOrNull { it.id == id })

    override fun observeRequirements(procedureId: Long): Flow<List<DocumentRequirement>> =
        flowOf(requirementsByProcedure[procedureId].orEmpty())
}

fun sampleProcedures(): List<Procedure> = listOf(
    Procedure(
        id = 1L,
        title = "Carte Nationale d'Identité (CNI)",
        category = com.ma_fantatra.domain.model.ProcedureCategory.CARTES_IDENTITE,
        cost = "2 000 Ariary",
        processingTime = "10 à 15 jours",
        description = "Demande de carte d'identité auprès du Fokontany.",
        instructions = "1. Se présenter au Fokontany.",
    ),
    Procedure(
        id = 2L,
        title = "Certificat de résidence",
        category = com.ma_fantatra.domain.model.ProcedureCategory.ATTESTATIONS,
        cost = "Gratuit",
        processingTime = "3 jours",
        description = "Justificatif de domicile délivré par le Fokontany.",
        instructions = "1. Remplir la demande.",
    ),
    Procedure(
        id = 3L,
        title = "Acte de mariage",
        category = com.ma_fantatra.domain.model.ProcedureCategory.ETAT_CIVIL,
        cost = "20 000 Ariary",
        processingTime = "1 mois",
        description = "Célébration et acte de mariage à la Commune.",
        instructions = "1. Déposer les dossiers.",
    ),
)