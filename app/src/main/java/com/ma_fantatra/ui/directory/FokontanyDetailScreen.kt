package com.ma_fantatra.ui.directory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ma_fantatra.R
import com.ma_fantatra.ui.components.LocationMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FokontanyDetailScreen(
    onBack: () -> Unit,
    viewModel: FokontanyDetailViewModel = hiltViewModel(),
) {
    val fokontany by viewModel.fokontany.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fokontany?.name.orEmpty()) },
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
        if (fokontany == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            val data = checkNotNull(fokontany)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = data.name,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                text = "${data.communeName} · ${data.districtName} · ${data.regionName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                            data.openingHours?.let { hours ->
                                Text(
                                    text = "${stringResource(R.string.opening_hours_label)} : $hours",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 12.dp),
                                )
                            }
                            data.addressNote?.let { address ->
                                Text(
                                    text = "${stringResource(R.string.address_label)} : $address",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                    }
                }
                if (data.latitude != null && data.longitude != null) {
                    item {
                        LocationMap(
                            latitude = data.latitude,
                            longitude = data.longitude,
                            name = data.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                        )
                    }
                }
            }
        }
    }
}