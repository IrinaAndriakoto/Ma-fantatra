package com.ma_fantatra.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ma_fantatra.R
import com.ma_fantatra.domain.model.DocumentRequirement
import com.ma_fantatra.ui.theme.Spacing
import com.ma_fantatra.ui.theme.detailPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcedureDetailScreen(
    onBack: () -> Unit,
    viewModel: ProcedureDetailViewModel = hiltViewModel(),
) {
    val procedure by viewModel.procedure.collectAsState()
    val checklist by viewModel.checklist.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(procedure?.title.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (procedure == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = detailPadding(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                item {
                    ProcedureSummary(
                        title = checkNotNull(procedure).title,
                        categoryLabel = checkNotNull(procedure).category.label,
                        description = checkNotNull(procedure).description,
                        cost = checkNotNull(procedure).cost,
                        processingTime = checkNotNull(procedure).processingTime,
                    )
                }

                item {
                    ChecklistSection(
                        checklist = checklist,
                        onToggle = viewModel::onToggle,
                    )
                }

                item {
                    StepsSection(instructions = checkNotNull(procedure).instructions)
                }
            }
        }
    }
}

@Composable
private fun ProcedureSummary(
    title: String,
    categoryLabel: String,
    description: String,
    cost: String,
    processingTime: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = Spacing.xs),
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.md))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.md))
            Text(
                text = "${stringResource(R.string.cost_label)} : $cost",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "${stringResource(R.string.processing_time_label)} : $processingTime",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
private fun ChecklistSection(
    checklist: ChecklistUiState,
    onToggle: (DocumentRequirement) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = stringResource(R.string.pieces_required_title),
                style = MaterialTheme.typography.titleMedium,
            )
            val progress = checklist.progress
            LinearProgressIndicator(
                progress = { progress.fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md),
            )
            Text(
                text = stringResource(R.string.pieces_progress, progress.checkedCount, progress.totalCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.sm),
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.sm))

            checklist.items.forEach { document ->
                DocumentRow(
                    document = document,
                    checked = checklist.isChecked(document.id),
                    onCheckedChange = { onToggle(document) },
                )
            }
        }
    }
}

@Composable
private fun DocumentRow(
    document: DocumentRequirement,
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
        )
        Column(modifier = Modifier.padding(start = Spacing.sm)) {
            Text(
                text = document.title,
                style = MaterialTheme.typography.bodyMedium.let {
                    if (checked) it.copy(fontWeight = FontWeight.Normal) else it
                },
            )
            document.note?.let { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(
                color = if (document.isMandatory) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.padding(top = Spacing.xs),
            ) {
                Text(
                    text = stringResource(
                        if (document.isMandatory) R.string.mandatory_badge else R.string.optional_badge,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                )
            }
        }
    }
}

@Composable
private fun StepsSection(instructions: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = stringResource(R.string.procedure_steps_title),
                style = MaterialTheme.typography.titleMedium,
            )
            val steps = instructions.lines().filter { it.isNotBlank() }
            steps.forEach { step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = Spacing.xs),
                )
            }
        }
    }
}