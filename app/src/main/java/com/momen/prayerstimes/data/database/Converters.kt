package com.momen.prayerstimes.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.momen.prayerstimes.data.model.Date
import com.momen.prayerstimes.data.model.Timings

class TimingsConverter {
    @TypeConverter
    fun fromTimings(timings: Timings): String {
        val gson = Gson()
        return gson.toJson(timings)
    }

    @TypeConverter
    fun toTimings(timingsString: String): Timings {
        val gson = Gson()
        return gson.fromJson(timingsString, Timings::class.java)
    }
}

class DateConverter {
    @TypeConverter
    fun fromDate(date: Date): String {
        return date.readable
    }

    @TypeConverter
    fun toDate(dateString: String): Date {
        return Date(dateString)
    }
}
