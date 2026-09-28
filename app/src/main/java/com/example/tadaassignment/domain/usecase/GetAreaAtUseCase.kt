package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.domain.session.TripDraftStore
import javax.inject.Inject

class GetAreaAtUseCase @Inject constructor(
    private val repository: SafeAreaRepository,
    private val tripDraftStore: TripDraftStore
) {
    suspend operator fun invoke(lat: Double, lng: Double): SafeLocation {
        val location = repository.getAreaAt(lat, lng)
        tripDraftStore.update { draft ->
            val moved = draft.markerLocation?.isSamePlaceAs(location) == false
            draft.copy(
                markerLocation = location,
                duplicateLocationError = if (moved) null else draft.duplicateLocationError
            )
        }
        return location
    }
}
