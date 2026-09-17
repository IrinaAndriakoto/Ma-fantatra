package com.ma_fantatra.ui.commune

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ma_fantatra.R
import com.ma_fantatra.domain.model.Commune
import com.ma_fantatra.ui.theme.Spacing
import com.ma_fantatra.ui.theme.screenPadding

@Composable
fun CommunesScreen(
    onCommuneClick: (Long) -> Unit,
    viewModel: CommuneViewModel = hiltViewModel(),
) {
    val sections by viewModel.sections.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = screenPadding(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (sections.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.communes_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(Spacing.xxl),
                )
            }
        }
        sections.forEach { section ->
            item(key = "header_${section.district}") {
                SectionHeader(text = section.district)
            }
            items(section.items, key = { it.id }) { commune ->
                CommuneCard(
                    commune = commune,
                    onClick = { onCommuneClick(commune.id) },
                )
            }
        }
    }
}

@Composable
private fun CommuneCard(
    commune: Commune,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = commune.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${commune.districtName} · ${commune.regionName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }
}

@Composable
internal fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.xs),
    )
}