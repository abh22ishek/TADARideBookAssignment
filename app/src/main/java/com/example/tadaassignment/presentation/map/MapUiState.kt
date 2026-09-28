package com.example.tadaassignment.presentation.map

import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.model.VButtonStep

data class MapUiState(
    val markerLocation: SafeLocation? = null,
    val isLoadingArea: Boolean = false,
    val slotA: SafeLocation? = null,
    val slotB: SafeLocation? = null,
    val vButtonStep: VButtonStep = VButtonStep.SET_A,
    val bookingResult: BookingResult? = null,
    val duplicateLocationError: String? = null,
    val isBooking: Boolean = false,
    val areaError: String? = null,
    val bookingError: String? = null
) {
    val pinMatchesSlotA: Boolean
        get() {
            val pin = markerLocation ?: return false
            val pickup = slotA ?: return false
            return pickup.isSamePlaceAs(pin)
        }

    val isPrimaryActionEnabled: Boolean
        get() = when (vButtonStep) {
            VButtonStep.SET_A -> markerLocation != null && !isLoadingArea
            VButtonStep.SET_B -> markerLocation != null && !isLoadingArea && !pinMatchesSlotA
            VButtonStep.BOOK -> !isBooking
        }

    val stepInstruction: String
        get() = when {
            isBooking -> "Booking your trip…"
            vButtonStep == VButtonStep.SET_A && isLoadingArea ->
                "Looking up the address under the pin…"
            vButtonStep == VButtonStep.SET_A ->
                "Move the map to your pickup, then tap Set A"
            vButtonStep == VButtonStep.SET_B && (isLoadingArea || pinMatchesSlotA) ->
                "Now move the map to a different drop-off, then tap Set B"
            vButtonStep == VButtonStep.SET_B ->
                "This pin will be drop-off B. Tap Set B to confirm"
            else -> "Pickup A and drop-off B are set. Tap Book to confirm"
        }

    val pinCaption: String?
        get() {
            val name = markerLocation?.displayName ?: return null
            return when (vButtonStep) {
                VButtonStep.SET_A -> "Pickup: $name"
                VButtonStep.SET_B -> "Drop-off: $name"
                VButtonStep.BOOK -> name
            }
        }
}
