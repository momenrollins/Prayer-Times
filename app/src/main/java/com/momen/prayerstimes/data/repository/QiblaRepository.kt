package com.momen.prayerstimes.data.repository

import com.momen.prayerstimes.data.model.QiblaDirectionData
import com.momen.prayerstimes.data.api.QiblaDirectionAPI
import javax.inject.Inject

class QiblaRepository @Inject constructor(private val qiblaDirectionAPI: QiblaDirectionAPI) {

    suspend fun getQiblaDirection(latitude: Double, longitude: Double): QiblaDirectionData {
        val response = qiblaDirectionAPI.getQiblaDirection(latitude, longitude)
        if (response.code == 200) {
            return response.data
        }
        throw Exception("Error fetching Qibla direction")
    }
}


