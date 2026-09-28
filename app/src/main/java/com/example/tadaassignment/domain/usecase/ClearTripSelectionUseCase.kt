package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.session.TripDraftStore
import javax.inject.Inject

class ClearTripSelectionUseCase @Inject constructor(
    private val tripDraftStore: TripDraftStore
) {
    operator fun invoke() {
        tripDraftStore.update { state ->
            state.copy(
                slotA = null,
                slotB = null,
                bookingResult = null,
                duplicateLocationError = null,
                nicknameError = null
            )
        }
    }
}
