package com.example.tadaassignment.presentation.cachedlocations

import androidx.lifecycle.ViewModel
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.session.TripDraftStore
import com.example.tadaassignment.domain.usecase.AssignSlotUseCase
import com.example.tadaassignment.domain.usecase.ClearDuplicateLocationErrorUseCase
import com.example.tadaassignment.domain.usecase.GetSavedLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CachedLocationsViewModel @Inject constructor(
    private val getSavedLocations: GetSavedLocationsUseCase,
    private val assignSlot: AssignSlotUseCase,
    private val clearDuplicateErrorUseCase: ClearDuplicateLocationErrorUseCase,
    private val tripDraftStore: TripDraftStore
) : ViewModel() {

    private val _duplicateError = MutableStateFlow(tripDraftStore.draft.value.duplicateLocationError)
    val duplicateError: StateFlow<String?> = _duplicateError.asStateFlow()

    fun locations(): List<SafeLocation> = getSavedLocations()

    fun select(slot: SafeAreaSlot, location: SafeLocation): Boolean {
        val accepted = assignSlot(slot, location)
        _duplicateError.value = tripDraftStore.draft.value.duplicateLocationError
        return accepted
    }

    fun clearDuplicateError() {
        clearDuplicateErrorUseCase()
        _duplicateError.value = null
    }
}
