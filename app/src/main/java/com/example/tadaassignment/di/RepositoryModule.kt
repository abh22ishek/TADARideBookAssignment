package com.example.tadaassignment.di

import com.example.tadaassignment.data.network.AndroidNetworkMonitor
import com.example.tadaassignment.data.repository.SafeAreaRepositoryImpl
import com.example.tadaassignment.data.session.InMemoryTripDraftStore
import com.example.tadaassignment.domain.network.NetworkMonitor
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.domain.session.TripDraftStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSafeAreaRepository(impl: SafeAreaRepositoryImpl): SafeAreaRepository

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(impl: AndroidNetworkMonitor): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindTripDraftStore(impl: InMemoryTripDraftStore): TripDraftStore
}
