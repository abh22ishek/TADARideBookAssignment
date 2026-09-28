package com.example.tadaassignment.presentation.map

import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.model.VButtonStep
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapUiStateTest {

    private val pickup = SafeLocation("a", "Koramangala", 12.935, 77.624, 45)
    private val dropOff = SafeLocation("b", "Indiranagar", 12.978, 77.640, 60)

    @Test
    fun setBStaysDisabledWhilePinIsStillOnPickup() {
        val state = MapUiState(
            markerLocation = pickup.copy(id = "pin"),
            slotA = pickup,
            vButtonStep = VButtonStep.SET_B
        )
        assertTrue(state.pinMatchesSlotA)
        assertFalse(state.isPrimaryActionEnabled)
        assertTrue(state.stepInstruction.contains("different drop-off"))
    }

    @Test
    fun setBEnablesOncePinMovesToANewPlace() {
        val state = MapUiState(
            markerLocation = dropOff,
            slotA = pickup,
            vButtonStep = VButtonStep.SET_B
        )
        assertFalse(state.pinMatchesSlotA)
        assertTrue(state.isPrimaryActionEnabled)
        assertEquals("This pin will be drop-off B. Tap Set B to confirm", state.stepInstruction)
    }
}
