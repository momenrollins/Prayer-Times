package com.momen.prayerstimes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.momen.prayerstimes.data.database.DateConverter
import com.momen.prayerstimes.data.database.TimingsConverter

data class PrayerTimeResponse(
    val code: Int,
    val `data`: List<PrayerTimes>,
    val status: String
)

@Entity(tableName = "prayer_times")
@TypeConverters(TimingsConverter::class, DateConverter::class)
data class PrayerTimes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timings: Timings,
    val date: Date
)

data class Timings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String,
)

data class Date(val readable: String)