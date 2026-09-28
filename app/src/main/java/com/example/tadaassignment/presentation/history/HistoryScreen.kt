package com.example.tadaassignment.presentation.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation

@Composable
fun HistoryScreen(
    bookings: List<BookingResult>,
    onBookingClicked: (BookingResult) -> Unit
) {
    val totalCount = bookings.size
    val totalPrice = bookings.sumOf { it.price }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "total count: $totalCount", fontWeight = FontWeight.Bold)
            Text(text = "total price: $totalPrice", fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(thickness = 2.dp)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(bookings, key = { it.id }) { booking ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookingClicked(booking) }
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    BookingRow(SafeAreaSlot.A, booking.locationA)
                    BookingRow(SafeAreaSlot.B, booking.locationB)
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun BookingRow(slot: SafeAreaSlot, location: SafeLocation) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = slot.label, fontWeight = FontWeight.Bold)
        Text(text = location.displayName, style = MaterialTheme.typography.bodyMedium)
    }
}
