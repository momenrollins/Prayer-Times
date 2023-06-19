package com.momen.prayerstimes.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.momen.prayerstimes.data.model.PrayerTimes

@Dao
interface PrayerTimesDao {
    @Query("SELECT * FROM prayer_times")
    suspend fun getPrayerTimes(): List<PrayerTimes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrayerTimes(prayerTimes: List<PrayerTimes>)
}
