package com.example.tadaassignment.presentation.booking

import androidx.lifecycle.ViewModel
import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.session.TripDraftStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    tripDraftStore: TripDraftStore
) : ViewModel() {

    val bookingResult: StateFlow<BookingResult?> = tripDraftStore.draft
        .map { it.bookingResult }
        .stateIn(viewModelScope, SharingStarted.Eagerly, tripDraftStore.draft.value.bookingResult)
}
