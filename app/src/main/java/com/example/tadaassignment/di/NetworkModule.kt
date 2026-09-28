package com.example.tadaassignment.di

import com.example.tadaassignment.data.remote.SafeAreaApiService
import com.example.tadaassignment.data.remote.aqicn.AqicnApiService
import com.example.tadaassignment.data.remote.geocode.GeocodeApiService
import com.example.tadaassignment.data.remote.mock.MockInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * The base URL is never actually hit - [MockInterceptor] short-circuits every
 * call with canned data. Point this at a real host and remove the interceptor
 * to switch to a live backend without touching any other layer.
 */
private const val MOCK_BASE_URL = "https://mock.tada.local/"

private const val AQICN_BASE_URL = "https://api.waqi.info/"

// Free, no-API-key reverse geocoding: https://www.bigdatacloud.com/reverse-geocoding/reverse-geocode-to-city-api
private const val GEOCODE_BASE_URL = "https://api.bigdatacloud.net/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideMockInterceptor(gson: Gson): MockInterceptor = MockInterceptor(gson)

    @Provides
    @Singleton
    fun provideOkHttpClient(mockInterceptor: MockInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(MOCK_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideSafeAreaApiService(retrofit: Retrofit): SafeAreaApiService =
        retrofit.create(SafeAreaApiService::class.java)

    // AQICN is a real, live API (unlike the mocked endpoints above), so this
    // gets its own plain OkHttp client with no mock interceptor attached.
    // Short timeouts so a slow/offline network fails fast instead of hanging
    // the whole location lookup for OkHttp's 10s default.
    @Provides
    @Singleton
    @Aqicn
    fun provideAqicnRetrofit(gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(AQICN_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAqicnApiService(@Aqicn retrofit: Retrofit): AqicnApiService =
        retrofit.create(AqicnApiService::class.java)

    // BigDataCloud's reverse-geocode-client endpoint - also real and live, no key needed.
    @Provides
    @Singleton
    @Geocode
    fun provideGeocodeRetrofit(gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(GEOCODE_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideGeocodeApiService(@Geocode retrofit: Retrofit): GeocodeApiService =
        retrofit.create(GeocodeApiService::class.java)
}
