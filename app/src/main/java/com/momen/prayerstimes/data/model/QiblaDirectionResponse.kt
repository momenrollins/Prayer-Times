package com.momen.prayerstimes.data.model

data class QiblaDirectionResponse(
    val code: Int,
    val status: String,
    val data: QiblaDirectionData
)

data class QiblaDirectionData(
    val latitude: Double,
    val longitude: Double,
    val direction: Double
)
