package com.momen.prayerstimes.data.api

import com.momen.prayerstimes.data.model.QiblaDirectionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QiblaDirectionAPI {
    @GET("v1/qibla/{latitude}/{longitude}")
    suspend fun getQiblaDirection(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): QiblaDirectionResponse
}

