package com.example.tadaassignment.presentation.map

import com.example.tadaassignment.data.session.InMemoryTripDraftStore
import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeAreaSlot
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.model.VButtonStep
import com.example.tadaassignment.domain.network.NetworkMonitor
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.domain.usecase.AssignSlotUseCase
import com.example.tadaassignment.domain.usecase.BookRouteUseCase
import com.example.tadaassignment.domain.usecase.ClearDuplicateLocationErrorUseCase
import com.example.tadaassignment.domain.usecase.ClearSlotUseCase
import com.example.tadaassignment.domain.usecase.ClearTripSelectionUseCase
import com.example.tadaassignment.domain.usecase.GetAreaAtUseCase
import com.example.tadaassignment.domain.usecase.SetNicknameUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeSafeAreaRepository
    private lateinit var network: FakeNetworkMonitor
    private lateinit var store: InMemoryTripDraftStore
    private lateinit var viewModel: MapViewModel
    private lateinit var setNickname: SetNicknameUseCase

    private val pinA = SafeLocation("a", "Koramangala", 12.935, 77.624, 45)
    private val pinB = SafeLocation("b", "Indiranagar", 12.978, 77.640, 60)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeSafeAreaRepository()
        network = FakeNetworkMonitor(online = true)
        store = InMemoryTripDraftStore()
        setNickname = SetNicknameUseCase(repository, store)
        viewModel = MapViewModel(
            getAreaAt = GetAreaAtUseCase(repository, store),
            assignSlot = AssignSlotUseCase(repository, store),
            clearSlot = ClearSlotUseCase(store),
            bookRoute = BookRouteUseCase(repository, store, network),
            clearTripSelection = ClearTripSelectionUseCase(store),
            clearDuplicateError = ClearDuplicateLocationErrorUseCase(store),
            tripDraftStore = store,
            networkMonitor = network
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun settingAThenSamePlaceForBShowsDuplicateError() {
        viewModel.setSlot(SafeAreaSlot.A, pinA)
        viewModel.setSlot(SafeAreaSlot.B, pinA.copy(id = "a-copy"))
        assertEquals("A and B can't be the same location", viewModel.uiState.value.duplicateLocationError)
        assertNull(viewModel.uiState.value.slotB)
        assertEquals(VButtonStep.SET_B, viewModel.uiState.value.vButtonStep)
    }

    @Test
    fun settingTwoDifferentPlacesEnablesBook() {
        viewModel.setSlot(SafeAreaSlot.A, pinA)
        viewModel.setSlot(SafeAreaSlot.B, pinB)
        assertEquals(VButtonStep.BOOK, viewModel.uiState.value.vButtonStep)
        assertNull(viewModel.uiState.value.duplicateLocationError)
    }

    @Test
    fun duplicateNicknameIsRejected() {
        viewModel.setSlot(SafeAreaSlot.A, pinA)
        viewModel.setSlot(SafeAreaSlot.B, pinB)
        setNickname(SafeAreaSlot.A, "home")
        setNickname(SafeAreaSlot.B, "HOME")
        assertTrue(store.draft.value.nicknameError!!.contains("already used"))
        assertNull(store.draft.value.slotB?.nickname)
    }

    @Test
    fun reSettingAKeepsExistingNickname() {
        viewModel.setSlot(SafeAreaSlot.A, pinA)
        setNickname(SafeAreaSlot.A, "home")
        viewModel.setSlot(SafeAreaSlot.A, pinA.copy(nickname = null))
        assertEquals("home", viewModel.uiState.value.slotA?.nickname)
    }

    @Test
    fun bookingOfflineSetsError() = runTest {
        viewModel.setSlot(SafeAreaSlot.A, pinA)
        viewModel.setSlot(SafeAreaSlot.B, pinB)
        network.online = false
        viewModel.retryBooking()
        assertEquals("No internet connection", viewModel.uiState.value.bookingError)
        assertNull(viewModel.uiState.value.bookingResult)
    }

    @Test
    fun bookingOnlineStoresResult() = runTest {
        viewModel.setSlot(SafeAreaSlot.A, pinA)
        viewModel.setSlot(SafeAreaSlot.B, pinB)
        viewModel.retryBooking()
        val result = viewModel.uiState.value.bookingResult
        assertEquals(pinA.name, result?.locationA?.name)
        assertEquals(pinB.name, result?.locationB?.name)
        assertTrue(result!!.price > 0)
    }
}

private class FakeNetworkMonitor(var online: Boolean) : NetworkMonitor {
    override fun isOnline(): Boolean = online
}

private class FakeSafeAreaRepository : SafeAreaRepository {
    private val saved = mutableListOf<SafeLocation>()

    override suspend fun getAreaAt(lat: Double, lng: Double): SafeLocation =
        SafeLocation("pin", "$lat,$lng", lat, lng, 40)

    override fun getCachedLocations(): List<SafeLocation> = saved.toList()

    override fun saveLocation(location: SafeLocation): SafeLocation {
        val existing = saved.find { it.isSamePlaceAs(location) }
        val toSave = location.copy(nickname = location.nickname ?: existing?.nickname)
        saved.removeAll { it.isSamePlaceAs(location) }
        saved.add(toSave)
        return toSave
    }

    override fun updateCachedLocation(location: SafeLocation) {
        saved.removeAll { it.isSamePlaceAs(location) }
        saved.add(location)
    }

    override suspend fun bookRoute(locationA: SafeLocation, locationB: SafeLocation): BookingResult =
        BookingResult("book-1", locationA, locationB, price = 12.5)

    override suspend fun getBooks(year: Int, month: Int): List<BookingResult> = emptyList()
}
