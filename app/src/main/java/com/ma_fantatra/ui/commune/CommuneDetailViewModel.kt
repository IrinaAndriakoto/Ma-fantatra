package com.ma_fantatra.ui.commune

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma_fantatra.domain.model.Commune
import com.ma_fantatra.domain.repository.CommuneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CommuneDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CommuneRepository,
) : ViewModel() {

    private val communeId: Long = checkNotNull(savedStateHandle["communeId"])

    val commune: StateFlow<Commune?> = repository.observeById(communeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(RESUME_TIMEOUT_MS), null)

    init {
        viewModelScope.launch { repository.refresh() }
    }

    private companion object {
        const val RESUME_TIMEOUT_MS = 5_000L
    }
}