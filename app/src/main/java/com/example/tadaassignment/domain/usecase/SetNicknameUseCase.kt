package com.example.tadaassignment.domain.usecase

import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.domain.session.TripDraftStore
import javax.inject.Inject

class SetNicknameUseCase @Inject constructor(
    private val repository: SafeAreaRepository,
    private val tripDraftStore: TripDraftStore
) {
    operator fun invoke(slot: SafeAreaSlot, nickname: String): Boolean {
        val nicknameOrNull = nickname.trim().ifBlank { null }
        if (nicknameOrNull != null && nicknameOrNull.length > NICKNAME_MAX_LENGTH) {
            tripDraftStore.update {
                it.copy(nicknameError = "Nickname can't be longer than $NICKNAME_MAX_LENGTH characters")
            }
            return false
        }
        val current = tripDraftStore.draft.value.locationFor(slot) ?: return false

        if (nicknameOrNull != null && isNicknameTaken(current, nicknameOrNull)) {
            tripDraftStore.update {
                it.copy(nicknameError = "\"$nicknameOrNull\" is already used by another location")
            }
            return false
        }

        val updated = current.copy(nickname = nicknameOrNull)
        repository.updateCachedLocation(updated)
        tripDraftStore.update { state ->
            when (slot) {
                SafeAreaSlot.A -> state.copy(slotA = updated, nicknameError = null)
                SafeAreaSlot.B -> state.copy(slotB = updated, nicknameError = null)
            }
        }
        return true
    }

    fun clearError() {
        tripDraftStore.update { it.copy(nicknameError = null) }
    }

    private fun isNicknameTaken(editing: SafeLocation, nickname: String): Boolean {
        val draft = tripDraftStore.draft.value
        val other = if (editing.id == draft.slotA?.id) draft.slotB else draft.slotA
        val candidates = repository.getCachedLocations() + listOfNotNull(other)
        return candidates.any { it.id != editing.id && it.nickname.equals(nickname, ignoreCase = true) }
    }
}
