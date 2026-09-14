package com.ma_fantatra.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma_fantatra.domain.model.ChecklistProgress
import com.ma_fantatra.domain.model.DocumentRequirement
import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.domain.model.computeChecklistProgress
import com.ma_fantatra.domain.repository.ChecklistRepository
import com.ma_fantatra.domain.repository.ProcedureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChecklistUiState(
    val items: List<DocumentRequirement>,
    val checkedDocumentIds: Set<Long>,
) {
    val progress: ChecklistProgress = computeChecklistProgress(items, checkedDocumentIds)

    fun isChecked(documentId: Long): Boolean = documentId in checkedDocumentIds
}

@HiltViewModel
class ProcedureDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val procedureRepository: ProcedureRepository,
    private val checklistRepository: ChecklistRepository,
) : ViewModel() {

    private val procedureId: Long = checkNotNull(savedStateHandle["procedureId"])

    val procedure: StateFlow<Procedure?> = procedureRepository.observeById(procedureId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(RESUME_TIMEOUT_MS), null)

    val checklist: StateFlow<ChecklistUiState> =
        combine(
            procedureRepository.observeRequirements(procedureId),
            checklistRepository.observeCheckedDocumentIds(),
        ) { requirements, checked ->
            ChecklistUiState(items = requirements, checkedDocumentIds = checked)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(RESUME_TIMEOUT_MS),
            ChecklistUiState(items = emptyList(), checkedDocumentIds = emptySet()),
        )

    fun onToggle(document: DocumentRequirement) {
        viewModelScope.launch {
            checklistRepository.setChecked(
                documentId = document.id,
                checked = document.id !in checklist.value.checkedDocumentIds,
            )
        }
    }

    init {
        viewModelScope.launch { procedureRepository.refresh() }
    }

    private companion object {
        const val RESUME_TIMEOUT_MS = 5_000L
    }
}