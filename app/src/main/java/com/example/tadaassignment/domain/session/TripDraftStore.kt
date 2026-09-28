package com.example.tadaassignment.domain.session

import com.example.tadaassignment.domain.model.TripDraft
import kotlinx.coroutines.flow.StateFlow

interface TripDraftStore {
    val draft: StateFlow<TripDraft>
    fun update(transform: (TripDraft) -> TripDraft)
}
