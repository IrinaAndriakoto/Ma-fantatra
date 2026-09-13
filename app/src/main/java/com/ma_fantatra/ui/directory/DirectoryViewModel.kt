package com.ma_fantatra.ui.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ma_fantatra.domain.model.Fokontany
import com.ma_fantatra.domain.repository.FokontanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DirectorySection(
    val region: String,
    val items: List<Fokontany>,
)

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    repository: FokontanyRepository,
) : ViewModel() {

    val sections: StateFlow<List<DirectorySection>> = repository.observeAll()
        .map { fokontanyList ->
            fokontanyList
                .groupBy { it.regionName }
                .map { (region, items) -> DirectorySection(region = region, items = items) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())
}