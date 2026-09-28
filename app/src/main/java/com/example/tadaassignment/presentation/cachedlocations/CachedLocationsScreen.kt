package com.example.tadaassignment.presentation.cachedlocations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tadaassignment.domain.model.SafeLocation

@Composable
fun CachedLocationsScreen(
    locations: List<SafeLocation>,
    errorMessage: String?,
    onLocationSelected: (SafeLocation) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }

        if (locations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Move the map, then tap Set A or Set B to save a place",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            }
            return@Column
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(locations, key = { it.id }) { location ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLocationSelected(location) }
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(text = location.displayName)
                    Text(
                        text = String.format(Locale.US, "%.3f, %.3f", location.lat, location.lng),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                HorizontalDivider()
            }
        }
    }
}
