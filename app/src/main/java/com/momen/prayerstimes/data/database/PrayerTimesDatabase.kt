package com.momen.prayerstimes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.momen.prayerstimes.data.database.PrayerTimesDao
import com.momen.prayerstimes.data.model.PrayerTimes

@Database(entities = [PrayerTimes::class], version = 1, exportSchema = false)
abstract class PrayerTimesDatabase : RoomDatabase() {
    abstract fun prayerTimesDao(): PrayerTimesDao
}
