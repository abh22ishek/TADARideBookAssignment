package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.session.TripDraftStore
import javax.inject.Inject

class ClearDuplicateLocationErrorUseCase @Inject constructor(
    private val tripDraftStore: TripDraftStore
) {
    operator fun invoke() {
        tripDraftStore.update { it.copy(duplicateLocationError = null) }
    }
}
