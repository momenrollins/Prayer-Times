package com.momen.prayerstimes.data.api

import com.momen.prayerstimes.data.model.PrayerTimeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerTimesAPI {
    @GET("v1/calendar/{year}/{month}")
    suspend fun getPrayerTimes(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Response<PrayerTimeResponse>
}
