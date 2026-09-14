package com.ma_fantatra.ui.commune

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma_fantatra.domain.model.Commune
import com.ma_fantatra.domain.repository.CommuneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CommuneSection(
    val district: String,
    val items: List<Commune>,
)

@HiltViewModel
class CommuneViewModel @Inject constructor(
    private val repository: CommuneRepository,
) : ViewModel() {

    val sections: StateFlow<List<CommuneSection>> = repository.observeAll()
        .map { communeList ->
            communeList
                .groupBy { it.districtName }
                .map { (district, items) -> CommuneSection(district = district, items = items) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    init {
        viewModelScope.launch { repository.refresh() }
    }
}