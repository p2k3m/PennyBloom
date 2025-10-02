package com.pennybloom.onboarding.data

import retrofit2.http.GET

interface PennyBloomApi {
    @GET("health")
    suspend fun healthCheck(): Unit
}
