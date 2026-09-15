package com.ma_fantatra.ui.directory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ma_fantatra.R
import com.ma_fantatra.domain.model.Fokontany

@Composable
fun DirectoryScreen(
    onFokontanyClick: (Long) -> Unit,
    viewModel: DirectoryViewModel = hiltViewModel(),
) {
    val sections by viewModel.sections.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (sections.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.directory_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(24.dp),
                )
            }
        }
        sections.forEach { section ->
            item(key = "header_${section.region}") {
                Text(
                    text = section.region,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            items(section.items, key = { it.id }) { fokontany ->
                FokontanyCard(
                    fokontany = fokontany,
                    onClick = { onFokontanyClick(fokontany.id) },
                )
            }
        }
    }
}

@Composable
private fun FokontanyCard(
    fokontany: Fokontany,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = fokontany.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${fokontany.communeName} · ${fokontany.districtName}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            fokontany.openingHours?.let { hours ->
                Text(
                    text = "${stringResource(R.string.opening_hours_label)} : $hours",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            fokontany.addressNote?.let { address ->
                Text(
                    text = "${stringResource(R.string.address_label)} : $address",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}