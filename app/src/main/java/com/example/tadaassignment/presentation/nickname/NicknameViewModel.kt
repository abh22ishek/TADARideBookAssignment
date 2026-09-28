package com.example.tadaassignment.presentation.nickname

import androidx.lifecycle.ViewModel
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.session.TripDraftStore
import com.example.tadaassignment.domain.usecase.SetNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val setNicknameUseCase: SetNicknameUseCase,
    private val tripDraftStore: TripDraftStore
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        _errorMessage.value = tripDraftStore.draft.value.nicknameError
    }

    fun locationFor(slot: SafeAreaSlot) = tripDraftStore.draft.value.locationFor(slot)

    fun confirm(slot: SafeAreaSlot, nickname: String): Boolean {
        val accepted = setNicknameUseCase(slot, nickname)
        _errorMessage.update { tripDraftStore.draft.value.nicknameError }
        return accepted
    }

    fun clearError() {
        setNicknameUseCase.clearError()
        _errorMessage.value = null
    }
}
