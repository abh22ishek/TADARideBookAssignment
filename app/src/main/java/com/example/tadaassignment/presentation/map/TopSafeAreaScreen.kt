package com.example.tadaassignment.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.presentation.components.AqiBadge
import com.example.tadaassignment.presentation.components.BottomActionBar
import com.example.tadaassignment.presentation.components.StatusBanner
import com.example.tadaassignment.ui.theme.AccentOrange
import com.example.tadaassignment.ui.theme.TopBarNavy
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun TopSafeAreaScreen(
    cameraPositionState: CameraPositionState,
    initialCameraLocation: LatLng?,
    hasLocationPermission: Boolean,
    onLabelClick: (SafeAreaSlot) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var hasCenteredOnUser by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(initialCameraLocation) {
        val location = initialCameraLocation
        if (location != null && !hasCenteredOnUser) {
            hasCenteredOnUser = true
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 16f)
        }
    }

    LaunchedEffect(cameraPositionState) {
        val target = cameraPositionState.position.target
        viewModel.onCameraIdle(target.latitude, target.longitude)

        snapshotFlow { cameraPositionState.isMoving }
            .distinctUntilChanged()
            .filter { isMoving -> !isMoving }
            .collect {
                val center = cameraPositionState.position.target
                viewModel.onCameraIdle(center.latitude, center.longitude)
            }
    }

    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true),
            contentPadding = PaddingValues(bottom = with(density) { bottomBarHeightPx.toDp() })
        ) {
            uiState.slotA?.let { pickup ->
                ConfirmedSlotMarker(
                    title = "A · Pickup",
                    location = pickup,
                    hue = BitmapDescriptorFactory.HUE_AZURE
                )
            }
            uiState.slotB?.let { dropOff ->
                ConfirmedSlotMarker(
                    title = "B · Drop-off",
                    location = dropOff,
                    hue = BitmapDescriptorFactory.HUE_ORANGE
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.Place,
            contentDescription = "Pin to confirm",
            modifier = Modifier.align(Alignment.Center).size(40.dp)
        )

        uiState.pinCaption?.let { caption ->
            Text(
                text = caption,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 28.dp)
                    .widthIn(max = 260.dp)
                    .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        AqiBadge(
            aqi = uiState.markerLocation?.aqi,
            isLoading = uiState.isLoadingArea,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoadingArea || uiState.isBooking) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = AccentOrange,
                    trackColor = TopBarNavy
                )
            }

            val duplicateError = uiState.duplicateLocationError
            val bookingError = uiState.bookingError
            val areaError = uiState.areaError
            val bannerModifier = Modifier.padding(top = 16.dp)
            when {
                duplicateError != null -> StatusBanner(
                    message = duplicateError,
                    onDismiss = viewModel::clearMapError,
                    modifier = bannerModifier
                )

                bookingError != null -> StatusBanner(
                    message = bookingError,
                    onRetry = viewModel::retryBooking,
                    onDismiss = viewModel::clearMapError,
                    modifier = bannerModifier
                )

                areaError != null -> StatusBanner(
                    message = areaError,
                    onRetry = viewModel::retryAreaLookup,
                    onDismiss = viewModel::clearMapError,
                    modifier = bannerModifier
                )
            }
        }

        BottomActionBar(
            instruction = uiState.stepInstruction,
            aLabelText = uiState.slotA?.displayName.orEmpty(),
            bLabelText = uiState.slotB?.displayName.orEmpty(),
            buttonLabel = uiState.vButtonStep.label,
            isButtonEnabled = uiState.isPrimaryActionEnabled,
            isBooking = uiState.isBooking,
            onLabelClick = onLabelClick,
            onResetClick = viewModel::resetSlot,
            onButtonClick = viewModel::onVButtonClicked,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates -> bottomBarHeightPx = coordinates.size.height }
        )
    }
}

@Composable
private fun ConfirmedSlotMarker(
    title: String,
    location: SafeLocation,
    hue: Float
) {
    val state = remember(location.lat, location.lng) {
        MarkerState(position = LatLng(location.lat, location.lng))
    }
    Marker(
        state = state,
        title = title,
        snippet = location.displayName,
        icon = BitmapDescriptorFactory.defaultMarker(hue)
    )
}
