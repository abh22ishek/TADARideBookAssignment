package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.domain.session.TripDraftStore
import javax.inject.Inject

class GetSavedLocationsUseCase @Inject constructor(
    private val repository: SafeAreaRepository,
    private val tripDraftStore: TripDraftStore
) {
    operator fun invoke(): List<SafeLocation> {
        val saved = repository.getCachedLocations()
        val current = tripDraftStore.draft.value.markerLocation ?: return saved
        return if (saved.any { it.isSamePlaceAs(current) }) saved else listOf(current) + saved
    }
}
