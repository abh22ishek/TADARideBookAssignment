package com.example.tadaassignment.presentation.nickname

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.usecase.NICKNAME_MAX_LENGTH
import com.example.tadaassignment.ui.theme.AccentOrange

@Composable
fun NicknameScreen(
    slot: SafeAreaSlot,
    location: SafeLocation,
    errorMessage: String?,
    onConfirm: (nickname: String) -> Unit
) {
    var nicknameInput by remember(location) { mutableStateOf(location.nickname.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = slot.label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(text = location.name, style = MaterialTheme.typography.titleMedium)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "aqi", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "${location.aqi}", fontWeight = FontWeight.Bold)
        }

        Column {
            OutlinedTextField(
                value = nicknameInput,
                onValueChange = { newValue ->
                    if (newValue.length <= NICKNAME_MAX_LENGTH) nicknameInput = newValue
                },
                label = { Text("nickname") },
                singleLine = true,
                isError = errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(AccentOrange, RoundedCornerShape(12.dp))
                .clickable { onConfirm(nicknameInput) },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "v", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
