package com.example.tadaassignment.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.usecase.BookingHistoryResult
import com.example.tadaassignment.domain.usecase.GetBookingHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val bookings: List<BookingResult>? = null,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getBookingHistory: GetBookingHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(bookings = null, error = null) }
            when (val result = getBookingHistory()) {
                BookingHistoryResult.Offline -> {
                    _uiState.update { it.copy(error = "No internet connection") }
                }
                is BookingHistoryResult.Success -> {
                    _uiState.update { it.copy(bookings = result.bookings, error = null) }
                }
                is BookingHistoryResult.Failure -> {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }
}
