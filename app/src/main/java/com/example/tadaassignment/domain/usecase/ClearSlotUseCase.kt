package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.session.TripDraftStore
import javax.inject.Inject

class ClearSlotUseCase @Inject constructor(
    private val tripDraftStore: TripDraftStore
) {
    operator fun invoke(slot: SafeAreaSlot) {
        tripDraftStore.update { state ->
            state.copy(
                slotA = if (slot == SafeAreaSlot.A) null else state.slotA,
                slotB = if (slot == SafeAreaSlot.B) null else state.slotB,
                duplicateLocationError = null
            )
        }
    }
}
