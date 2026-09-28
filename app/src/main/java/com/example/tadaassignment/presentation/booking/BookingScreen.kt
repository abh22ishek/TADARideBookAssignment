package com.example.tadaassignment.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.ui.theme.AccentOrange

@Composable
fun BookingScreen(
    bookingResult: BookingResult,
    onNextClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        BookingResultContent(bookingResult = bookingResult, modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(52.dp)
                .background(AccentOrange, RoundedCornerShape(12.dp))
                .clickable(onClick = onNextClicked),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "v", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BookingResultContent(bookingResult: BookingResult, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        LocationSummary(SafeAreaSlot.A, bookingResult.locationA)
        HorizontalDivider()
        LocationSummary(SafeAreaSlot.B, bookingResult.locationB)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "price", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "${bookingResult.price}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LocationSummary(slot: SafeAreaSlot, location: SafeLocation) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = slot.label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 16.dp))
            Text(text = location.name, style = MaterialTheme.typography.titleMedium)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "aqi", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "${location.aqi}")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "nickname", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = location.nickname.orEmpty())
        }
    }
}
