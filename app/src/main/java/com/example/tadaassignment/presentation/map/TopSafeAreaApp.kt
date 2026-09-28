package com.example.tadaassignment.presentation.map

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tadaassignment.data.remote.mock.MockDataSource
import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.presentation.booking.BookingResultContent
import com.example.tadaassignment.presentation.booking.BookingScreen
import com.example.tadaassignment.presentation.booking.BookingViewModel
import com.example.tadaassignment.presentation.cachedlocations.CachedLocationsScreen
import com.example.tadaassignment.presentation.cachedlocations.CachedLocationsViewModel
import com.example.tadaassignment.presentation.history.HistoryScreen
import com.example.tadaassignment.presentation.history.HistoryViewModel
import com.example.tadaassignment.presentation.nickname.NicknameScreen
import com.example.tadaassignment.presentation.nickname.NicknameViewModel
import com.example.tadaassignment.ui.theme.StatusBarNavyDark
import com.example.tadaassignment.ui.theme.TopBarNavy
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState

private sealed interface Destination {
    data object Map : Destination
    data class Nickname(val slot: SafeAreaSlot) : Destination
    data class CachedLocations(val slot: SafeAreaSlot) : Destination
    data object Booking : Destination
    data object History : Destination
    data class HistoryDetail(val booking: BookingResult) : Destination
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSafeAreaApp(
    initialCameraLocation: LatLng?,
    hasLocationPermission: Boolean
) {
    val mapViewModel: MapViewModel = hiltViewModel()
    val nicknameViewModel: NicknameViewModel = hiltViewModel()
    val cachedLocationsViewModel: CachedLocationsViewModel = hiltViewModel()
    val bookingViewModel: BookingViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()

    val mapUiState by mapViewModel.uiState.collectAsState()
    var destination by remember { mutableStateOf<Destination>(Destination.Map) }

    LaunchedEffect(mapUiState.bookingResult, mapUiState.isBooking) {
        if (mapUiState.bookingResult != null && !mapUiState.isBooking) {
            destination = Destination.Booking
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        val start = initialCameraLocation ?: LatLng(MockDataSource.DEFAULT_LAT, MockDataSource.DEFAULT_LNG)
        position = CameraPosition.fromLatLngZoom(start, 16f)
    }

    val goToMap = {
        if (mapUiState.bookingResult != null) {
            mapViewModel.clearBookingSelection()
        }
        destination = Destination.Map
    }

    val onBack = {
        if (destination is Destination.HistoryDetail) {
            destination = Destination.History
        } else {
            goToMap()
        }
    }

    BackHandler(enabled = destination !is Destination.Map, onBack = onBack)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(destination.title) },
                    navigationIcon = {
                        if (destination !is Destination.Map) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TopBarNavy,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                Column {
                    HorizontalDivider()
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        NavigationBarItem(
                            selected = destination !is Destination.History && destination !is Destination.HistoryDetail,
                            onClick = goToMap,
                            icon = { Icon(Icons.Filled.Place, contentDescription = null) },
                            label = { Text("Map") }
                        )
                        NavigationBarItem(
                            selected = destination is Destination.History || destination is Destination.HistoryDetail,
                            onClick = {
                                historyViewModel.load()
                                destination = Destination.History
                            },
                            icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                            label = { Text("History") }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                AppDestination(
                    destination = destination,
                    onDestinationChange = { destination = it },
                    cameraPositionState = cameraPositionState,
                    initialCameraLocation = initialCameraLocation,
                    hasLocationPermission = hasLocationPermission,
                    mapViewModel = mapViewModel,
                    nicknameViewModel = nicknameViewModel,
                    cachedLocationsViewModel = cachedLocationsViewModel,
                    bookingViewModel = bookingViewModel,
                    historyViewModel = historyViewModel
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(StatusBarNavyDark)
                .align(Alignment.TopStart)
        )
    }
}

@Composable
private fun AppDestination(
    destination: Destination,
    onDestinationChange: (Destination) -> Unit,
    cameraPositionState: CameraPositionState,
    initialCameraLocation: LatLng?,
    hasLocationPermission: Boolean,
    mapViewModel: MapViewModel,
    nicknameViewModel: NicknameViewModel,
    cachedLocationsViewModel: CachedLocationsViewModel,
    bookingViewModel: BookingViewModel,
    historyViewModel: HistoryViewModel
) {
    when (val current = destination) {
        is Destination.Map -> TopSafeAreaScreen(
            cameraPositionState = cameraPositionState,
            initialCameraLocation = initialCameraLocation,
            hasLocationPermission = hasLocationPermission,
            viewModel = mapViewModel,
            onLabelClick = { slot ->
                val isAlreadySet = mapViewModel.locationForSlot(slot) != null
                onDestinationChange(
                    if (isAlreadySet) {
                        nicknameViewModel.clearError()
                        Destination.Nickname(slot)
                    } else {
                        cachedLocationsViewModel.clearDuplicateError()
                        Destination.CachedLocations(slot)
                    }
                )
            }
        )

        is Destination.Nickname -> {
            val location = nicknameViewModel.locationFor(current.slot)
            val errorMessage by nicknameViewModel.errorMessage.collectAsState()
            if (location != null) {
                NicknameScreen(
                    slot = current.slot,
                    location = location,
                    errorMessage = errorMessage,
                    onConfirm = { nickname ->
                        if (nicknameViewModel.confirm(current.slot, nickname)) {
                            onDestinationChange(Destination.Map)
                        }
                    }
                )
            } else {
                onDestinationChange(Destination.Map)
            }
        }

        is Destination.CachedLocations -> {
            val errorMessage by cachedLocationsViewModel.duplicateError.collectAsState()
            CachedLocationsScreen(
                locations = cachedLocationsViewModel.locations(),
                errorMessage = errorMessage,
                onLocationSelected = { location ->
                    if (cachedLocationsViewModel.select(current.slot, location)) {
                        onDestinationChange(Destination.Map)
                    }
                }
            )
        }

        is Destination.Booking -> {
            val bookingResult by bookingViewModel.bookingResult.collectAsState()
            val result = bookingResult
            if (result != null) {
                BookingScreen(
                    bookingResult = result,
                    onNextClicked = {
                        historyViewModel.load()
                        onDestinationChange(Destination.History)
                    }
                )
            } else {
                onDestinationChange(Destination.Map)
            }
        }

        is Destination.History -> {
            val historyState by historyViewModel.uiState.collectAsState()
            val historyError = historyState.error
            val history = historyState.bookings
            when {
                historyError != null -> MessageState(
                    message = historyError,
                    isError = true,
                    onRetry = historyViewModel::load
                )

                history != null && history.isEmpty() -> MessageState(
                    message = "No bookings this month yet.",
                    isError = false,
                    onRetry = historyViewModel::load
                )

                history != null -> HistoryScreen(
                    bookings = history,
                    onBookingClicked = { booking -> onDestinationChange(Destination.HistoryDetail(booking)) }
                )

                else -> Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        is Destination.HistoryDetail -> BookingResultContent(bookingResult = current.booking)
    }
}

@Composable
private fun MessageState(
    message: String,
    isError: Boolean,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = message,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            TextButton(onClick = onRetry) { Text(text = "Retry") }
        }
    }
}

private val Destination.title: String
    get() = when (this) {
        Destination.Map -> "Map"
        is Destination.Nickname -> "Nickname"
        is Destination.CachedLocations -> "Saved locations"
        Destination.Booking -> "Booking"
        Destination.History -> "History"
        is Destination.HistoryDetail -> "Booking details"
    }
