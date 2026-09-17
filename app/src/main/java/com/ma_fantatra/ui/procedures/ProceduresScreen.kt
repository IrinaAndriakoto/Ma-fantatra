package com.ma_fantatra.ui.procedures

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ma_fantatra.R
import com.ma_fantatra.domain.model.Procedure
import com.ma_fantatra.ui.theme.Spacing
import com.ma_fantatra.ui.theme.screenPadding

@Composable
fun ProceduresScreen(
    onProcedureClick: (Long) -> Unit,
    viewModel: ProcedureListViewModel = hiltViewModel(),
) {
    val procedures by viewModel.filteredProcedures.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            singleLine = true,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { viewModel.onCategorySelected(null) },
                label = { Text(stringResource(R.string.filter_all)) },
            )
            viewModel.categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.onCategorySelected(category) },
                    label = { Text(category.label) },
                )
            }
        }

        if (procedures.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(
                    text = stringResource(R.string.empty_results),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(Spacing.xxl),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = screenPadding(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(procedures, key = { it.id }) { procedure ->
                    ProcedureCard(procedure = procedure, onClick = { onProcedureClick(procedure.id) })
                }
            }
        }
    }
}

@Composable
private fun ProcedureCard(
    procedure: Procedure,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = procedure.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = procedure.category.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = Spacing.xs),
            )
            Text(
                text = stringResource(R.string.cost_label) + " : ${procedure.cost}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Spacing.md),
            )
            Text(
                text = stringResource(R.string.processing_time_label) + " : ${procedure.processingTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}