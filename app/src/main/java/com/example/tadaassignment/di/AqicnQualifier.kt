package com.example.tadaassignment.di

import javax.inject.Qualifier

/** Distinguishes the real AQICN Retrofit/OkHttp instance from the mocked one. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Aqicn
