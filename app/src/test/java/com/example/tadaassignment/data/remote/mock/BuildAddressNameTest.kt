package com.example.tadaassignment.data.remote.mock

import org.junit.Assert.assertEquals
import org.junit.Test

class BuildAddressNameTest {

    @Test
    fun usesTwoMostSpecificAdministrativeNames() {
        val name = buildAddressName(BookMockData.localityInfo.administrative.orEmpty())
        assertEquals("Seocho District, Yangjae 2(i)-dong", name)
    }
}
