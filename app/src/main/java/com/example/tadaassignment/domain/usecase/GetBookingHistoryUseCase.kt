package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.network.NetworkMonitor
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import kotlinx.coroutines.CancellationException
import java.util.Calendar
import javax.inject.Inject

sealed interface BookingHistoryResult {
    data object Offline : BookingHistoryResult
    data class Success(val bookings: List<BookingResult>) : BookingHistoryResult
    data class Failure(val message: String) : BookingHistoryResult
}

class GetBookingHistoryUseCase @Inject constructor(
    private val repository: SafeAreaRepository,
    private val networkMonitor: NetworkMonitor
) {
    suspend operator fun invoke(): BookingHistoryResult {
        if (!networkMonitor.isOnline()) return BookingHistoryResult.Offline
        return try {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR)
            val month = now.get(Calendar.MONTH) + 1
            BookingHistoryResult.Success(repository.getBooks(year, month))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            BookingHistoryResult.Failure(
                userFacingError(error, networkMonitor.isOnline(), "Couldn't load history. Try again.")
            )
        }
    }
}
