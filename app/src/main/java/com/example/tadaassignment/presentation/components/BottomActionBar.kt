package com.example.tadaassignment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.ui.theme.AccentOrange

/**
 * Bottom bar: pickup/drop-off rows plus the step button (Set A → Set B → Book).
 */
@Composable
fun BottomActionBar(
    instruction: String,
    aLabelText: String,
    bLabelText: String,
    buttonLabel: String,
    isButtonEnabled: Boolean,
    isBooking: Boolean,
    onLabelClick: (SafeAreaSlot) -> Unit,
    onResetClick: (SafeAreaSlot) -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = instruction,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth().height(116.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LabelPill(
                    slotLetter = "A",
                    placeholder = "Pickup",
                    text = aLabelText,
                    onClick = { onLabelClick(SafeAreaSlot.A) },
                    onReset = { onResetClick(SafeAreaSlot.A) },
                    modifier = Modifier.weight(1f)
                )
                LabelPill(
                    slotLetter = "B",
                    placeholder = "Drop-off",
                    text = bLabelText,
                    onClick = { onLabelClick(SafeAreaSlot.B) },
                    onReset = { onResetClick(SafeAreaSlot.B) },
                    modifier = Modifier.weight(1f)
                )
            }

            Column(
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .background(
                        if (isButtonEnabled || isBooking) AccentOrange else AccentOrange.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = isButtonEnabled && !isBooking, onClick = onButtonClick),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isBooking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = buttonLabel, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LabelPill(
    slotLetter: String,
    placeholder: String,
    text: String,
    onClick: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSet = text.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = if (isSet) 4.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSet) {
            Text(text = slotLetter, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 16.dp))
            Text(text = text, modifier = Modifier.weight(1f))
            IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Clear $slotLetter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
