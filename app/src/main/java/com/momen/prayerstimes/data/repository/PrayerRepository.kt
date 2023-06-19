package com.momen.prayerstimes.data.repository

import com.momen.prayerstimes.data.database.PrayerTimesDao
import com.momen.prayerstimes.data.model.PrayerTimes
import com.momen.prayerstimes.data.api.PrayerTimesAPI
import javax.inject.Inject

class PrayerRepository @Inject constructor(
    private val prayerTimesAPI: PrayerTimesAPI,
    private val prayerTimesDao: PrayerTimesDao
) {
    private var cachedPrayerTimes: List<PrayerTimes>? = null

    suspend fun getPrayerTimes(latitude: Double, longitude: Double, year: Int, month: Int): List<PrayerTimes> {
        // Check if cached prayer times exist
        if (cachedPrayerTimes != null) {
            return cachedPrayerTimes!!
        } else {
            // Try to fetch prayer times from the local database
            val cachedPrayerTimes = prayerTimesDao.getPrayerTimes()
            if (cachedPrayerTimes.isNotEmpty()) {
                this.cachedPrayerTimes = cachedPrayerTimes
                return cachedPrayerTimes
            } else {
                // Fetch prayer times from the API
                val response = prayerTimesAPI.getPrayerTimes(year, month, latitude, longitude)
                if (response.isSuccessful) {
                    val prayerTimesResponse = response.body()
                    val prayerTimes = prayerTimesResponse?.data ?: emptyList()
                    // Cache the retrieved prayer times locally
                    prayerTimesDao.savePrayerTimes(prayerTimes)
                    this.cachedPrayerTimes = prayerTimes
                    return prayerTimes
                } else {
                    throw Exception("Error fetching prayer times")
                }
            }
        }
    }
}
