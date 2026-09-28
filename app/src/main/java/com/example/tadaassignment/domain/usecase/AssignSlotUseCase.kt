package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.domain.session.TripDraftStore
import javax.inject.Inject

class AssignSlotUseCase @Inject constructor(
    private val repository: SafeAreaRepository,
    private val tripDraftStore: TripDraftStore
) {
    operator fun invoke(slot: SafeAreaSlot, location: SafeLocation): Boolean {
        val draft = tripDraftStore.draft.value
        val other = when (slot) {
            SafeAreaSlot.A -> draft.slotB
            SafeAreaSlot.B -> draft.slotA
        }
        if (other != null && other.isSamePlaceAs(location)) {
            tripDraftStore.update { it.copy(duplicateLocationError = "A and B can't be the same location") }
            return false
        }

        val saved = repository.saveLocation(location)
        tripDraftStore.update { state ->
            state.copy(
                slotA = if (slot == SafeAreaSlot.A) saved else state.slotA,
                slotB = if (slot == SafeAreaSlot.B) saved else state.slotB,
                duplicateLocationError = null
            )
        }
        return true
    }
}
