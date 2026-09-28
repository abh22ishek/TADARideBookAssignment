package com.example.tadaassignment.data.remote.aqicn

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AqicnStationDistanceTest {

    @Test
    fun shanghaiIsNotNearBangalore() {
        val shanghai = AqicnData(
            aqi = null,
            city = AqicnCity(geo = listOf(31.2047372, 121.4489017))
        )
        assertFalse(shanghai.isNear(12.9716, 77.5946))
    }

    @Test
    fun nearbyStationIsAccepted() {
        val nearby = AqicnData(
            aqi = null,
            city = AqicnCity(geo = listOf(12.9720, 77.5950))
        )
        assertTrue(nearby.isNear(12.9716, 77.5946))
    }
}
