package com.example.tadaassignment.data.session

import com.example.tadaassignment.domain.model.TripDraft
import com.example.tadaassignment.domain.session.TripDraftStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryTripDraftStore @Inject constructor() : TripDraftStore {
    private val _draft = MutableStateFlow(TripDraft())
    override val draft: StateFlow<TripDraft> = _draft.asStateFlow()

    override fun update(transform: (TripDraft) -> TripDraft) {
        _draft.update(transform)
    }
}
