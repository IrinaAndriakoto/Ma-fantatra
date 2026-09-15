package com.ma_fantatra.ui.directory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma_fantatra.domain.model.Fokontany
import com.ma_fantatra.domain.repository.FokontanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FokontanyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FokontanyRepository,
) : ViewModel() {

    private val fokontanyId: Long = checkNotNull(savedStateHandle["fokontanyId"])

    val fokontany: StateFlow<Fokontany?> = repository.observeById(fokontanyId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(RESUME_TIMEOUT_MS), null)

    init {
        viewModelScope.launch { repository.refresh() }
    }

    private companion object {
        const val RESUME_TIMEOUT_MS = 5_000L
    }
}