package com.example.tadaassignment.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeLocationTest {

    @Test
    fun nearbyCoordinatesCountAsTheSamePlace() {
        val a = SafeLocation("1", "A", 12.971000, 77.594000, 40)
        val b = SafeLocation("2", "B", 12.971400, 77.594200, 41)
        assertTrue(a.isSamePlaceAs(b))
    }

    @Test
    fun distantCoordinatesAreDifferentPlaces() {
        val a = SafeLocation("1", "A", 12.971, 77.594, 40)
        val b = SafeLocation("2", "B", 13.082, 77.625, 50)
        assertFalse(a.isSamePlaceAs(b))
    }

    @Test
    fun displayNamePrefersNickname() {
        val location = SafeLocation("1", "MG Road", 12.97, 77.59, 40, nickname = "home")
        assertEquals("home", location.displayName)
    }
}
