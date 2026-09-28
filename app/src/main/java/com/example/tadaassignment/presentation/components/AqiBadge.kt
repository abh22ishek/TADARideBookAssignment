package com.example.tadaassignment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * AQI for whatever is under the center pin. [aqi] is null until the first
 * lookup lands, and [isLoading] is true while one is in flight - showing a
 * literal "0" in either case would read as genuinely clean air.
 */
@Composable
fun AqiBadge(aqi: Int?, isLoading: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "aqi", style = MaterialTheme.typography.bodyMedium)
        when {
            // A stale value stays visible while the next one loads, so the
            // badge doesn't flicker between a number and a spinner on a pan.
            isLoading && aqi == null -> CircularProgressIndicator(
                modifier = Modifier.padding(start = 8.dp).size(14.dp),
                strokeWidth = 2.dp
            )

            else -> Text(
                text = "  ${aqi ?: "--"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
