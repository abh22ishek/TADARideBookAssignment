package com.example.tadaassignment.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.model.TripDraft
import com.example.tadaassignment.domain.model.VButtonStep
import com.example.tadaassignment.domain.session.TripDraftStore
import com.example.tadaassignment.domain.usecase.AssignSlotUseCase
import com.example.tadaassignment.domain.usecase.BookRouteResult
import com.example.tadaassignment.domain.usecase.BookRouteUseCase
import com.example.tadaassignment.domain.usecase.ClearDuplicateLocationErrorUseCase
import com.example.tadaassignment.domain.usecase.ClearSlotUseCase
import com.example.tadaassignment.domain.usecase.ClearTripSelectionUseCase
import com.example.tadaassignment.domain.usecase.GetAreaAtUseCase
import com.example.tadaassignment.domain.usecase.userFacingError
import com.example.tadaassignment.domain.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Shortest time a loading indicator stays up, so it never flickers past. */
private const val MIN_LOADING_VISIBLE_MS = 450L

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAreaAt: GetAreaAtUseCase,
    private val assignSlot: AssignSlotUseCase,
    private val clearSlot: ClearSlotUseCase,
    private val bookRoute: BookRouteUseCase,
    private val clearTripSelection: ClearTripSelectionUseCase,
    private val clearDuplicateError: ClearDuplicateLocationErrorUseCase,
    private val tripDraftStore: TripDraftStore,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var lastCameraTarget: Pair<Double, Double>? = null
    private var areaJob: Job? = null

    init {
        viewModelScope.launch {
            tripDraftStore.draft.collect { draft ->
                _uiState.update { it.merge(draft) }
            }
        }
    }

    fun onCameraIdle(lat: Double, lng: Double) {
        val isSameTarget = lastCameraTarget == lat to lng
        if (isSameTarget && _uiState.value.markerLocation != null && _uiState.value.areaError == null) return
        loadArea(lat, lng)
    }

    fun retryAreaLookup() {
        val (lat, lng) = lastCameraTarget ?: return
        loadArea(lat, lng)
    }

    private fun loadArea(lat: Double, lng: Double) {
        lastCameraTarget = lat to lng
        areaJob?.cancel()
        _uiState.update { it.copy(isLoadingArea = true, areaError = null) }
        areaJob = viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            val result = runCatching { getAreaAt(lat, lng) }
            val elapsed = System.currentTimeMillis() - startedAt
            if (elapsed < MIN_LOADING_VISIBLE_MS) delay(MIN_LOADING_VISIBLE_MS - elapsed)
            result
                .onSuccess {
                    _uiState.update { state ->
                        val moved = state.markerLocation?.isSamePlaceAs(it) == false
                        state.copy(
                            isLoadingArea = false,
                            areaError = null,
                            bookingError = if (moved) null else state.bookingError
                        )
                    }
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    _uiState.update { state ->
                        state.copy(
                            isLoadingArea = false,
                            areaError = userFacingError(
                                error,
                                networkMonitor.isOnline(),
                                "Couldn't look up this location"
                            )
                        )
                    }
                }
        }
    }

    fun onVButtonClicked() {
        val marker = _uiState.value.markerLocation ?: return
        when (_uiState.value.vButtonStep) {
            VButtonStep.SET_A -> setSlot(SafeAreaSlot.A, marker)
            VButtonStep.SET_B -> setSlot(SafeAreaSlot.B, marker)
            VButtonStep.BOOK -> onBookClicked()
        }
    }

    fun setSlot(slot: SafeAreaSlot, location: SafeLocation) {
        assignSlot(slot, location)
    }

    fun resetSlot(slot: SafeAreaSlot) {
        clearSlot(slot)
    }

    private fun onBookClicked() {
        if (_uiState.value.isBooking) return
        _uiState.update { it.copy(isBooking = true, bookingError = null) }
        viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            val result = bookRoute()
            val elapsed = System.currentTimeMillis() - startedAt
            if (result is BookRouteResult.Success || result is BookRouteResult.Failure) {
                if (elapsed < MIN_LOADING_VISIBLE_MS) delay(MIN_LOADING_VISIBLE_MS - elapsed)
            }
            when (result) {
                BookRouteResult.MissingSlots -> {
                    _uiState.update { it.copy(isBooking = false) }
                }
                BookRouteResult.Offline -> {
                    _uiState.update { it.copy(isBooking = false, bookingError = "No internet connection") }
                }
                is BookRouteResult.Success -> {
                    _uiState.update { it.copy(isBooking = false) }
                }
                is BookRouteResult.Failure -> {
                    _uiState.update { it.copy(isBooking = false, bookingError = result.message) }
                }
            }
        }
    }

    fun retryBooking() = onBookClicked()

    fun clearBookingSelection() {
        clearTripSelection()
        _uiState.update { it.copy(isBooking = false, bookingError = null, areaError = null) }
    }

    fun locationForSlot(slot: SafeAreaSlot) = tripDraftStore.draft.value.locationFor(slot)

    fun clearMapError() {
        clearDuplicateError()
        _uiState.update { it.copy(areaError = null, bookingError = null) }
    }

    private fun MapUiState.merge(draft: TripDraft): MapUiState = copy(
        markerLocation = draft.markerLocation,
        slotA = draft.slotA,
        slotB = draft.slotB,
        vButtonStep = draft.vButtonStep,
        bookingResult = draft.bookingResult,
        duplicateLocationError = draft.duplicateLocationError
    )
}
