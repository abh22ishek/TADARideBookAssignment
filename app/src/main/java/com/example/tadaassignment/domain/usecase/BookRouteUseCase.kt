package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.network.NetworkMonitor
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.domain.session.TripDraftStore
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

sealed interface BookRouteResult {
    data object MissingSlots : BookRouteResult
    data object Offline : BookRouteResult
    data class Success(val booking: BookingResult) : BookRouteResult
    data class Failure(val message: String) : BookRouteResult
}

class BookRouteUseCase @Inject constructor(
    private val repository: SafeAreaRepository,
    private val tripDraftStore: TripDraftStore,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(): BookRouteResult {
        val draft = tripDraftStore.draft.value
        val locationA = draft.slotA ?: return BookRouteResult.MissingSlots
        val locationB = draft.slotB ?: return BookRouteResult.MissingSlots
        if (!networkMonitor.isOnline()) return BookRouteResult.Offline
        return try {
            val result = repository.bookRoute(locationA, locationB)
            tripDraftStore.update { it.copy(bookingResult = result) }
            BookRouteResult.Success(result)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            BookRouteResult.Failure(
                userFacingError(error, networkMonitor.isOnline(), "Couldn't complete booking. Try again.")
            )
        }
    }
}
