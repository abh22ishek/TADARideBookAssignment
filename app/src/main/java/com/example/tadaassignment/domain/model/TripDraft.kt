package com.example.tadaassignment.domain.model

/**
 * Shared A/B booking draft. Lives outside any one screen ViewModel so Map,
 * Nickname, Saved locations, and Booking all see the same selection.
 */
data class TripDraft(
    val markerLocation: SafeLocation? = null,
    val slotA: SafeLocation? = null,
    val slotB: SafeLocation? = null,
    val bookingResult: BookingResult? = null,
    val duplicateLocationError: String? = null,
    val nicknameError: String? = null
) {
    val vButtonStep: VButtonStep get() = nextButtonStep(slotA, slotB)

    fun locationFor(slot: SafeAreaSlot): SafeLocation? = when (slot) {
        SafeAreaSlot.A -> slotA
        SafeAreaSlot.B -> slotB
    }
}
