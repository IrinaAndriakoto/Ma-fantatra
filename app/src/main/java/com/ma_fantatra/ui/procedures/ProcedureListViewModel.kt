package com.ma_fantatra.ui.procedures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.domain.model.ProcedureCategory
import com.ma_fantatra.domain.repository.ProcedureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProcedureListViewModel @Inject constructor(
    private val repository: ProcedureRepository,
) : ViewModel() {

    val categories: List<ProcedureCategory> = ProcedureCategory.entries

    private val allProcedures: StateFlow<List<Procedure>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(RESUME_TIMEOUT_MS), emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<ProcedureCategory?>(null)
    val selectedCategory: StateFlow<ProcedureCategory?> = _selectedCategory.asStateFlow()

    val filteredProcedures: StateFlow<List<Procedure>> =
        combine(allProcedures, _query, _selectedCategory) { procedures, query, category ->
            val trimmed = query.trim()
            procedures.filter { procedure ->
                (category == null || procedure.category == category) &&
                    (trimmed.isEmpty() ||
                        procedure.title.contains(trimmed, ignoreCase = true) ||
                        procedure.description.contains(trimmed, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(RESUME_TIMEOUT_MS), emptyList())

    fun onQueryChanged(value: String) {
        _query.value = value
    }

    fun onCategorySelected(category: ProcedureCategory?) {
        _selectedCategory.value = category
    }

    init {
        viewModelScope.launch { repository.refresh() }
    }

    private companion object {
        const val RESUME_TIMEOUT_MS = 5_000L
    }
}