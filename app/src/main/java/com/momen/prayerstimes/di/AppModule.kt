package com.momen.prayerstimes.di

import android.app.Application
import androidx.room.Room
import com.momen.prayerstimes.data.database.PrayerTimesDao
import com.momen.prayerstimes.data.database.PrayerTimesDatabase
import com.momen.prayerstimes.data.api.PrayerTimesAPI
import com.momen.prayerstimes.data.api.QiblaDirectionAPI
import com.momen.prayerstimes.data.repository.PrayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePrayerTimesAPI(): PrayerTimesAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PrayerTimesAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideQiblaDirectionAPI(): QiblaDirectionAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(QiblaDirectionAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): PrayerTimesDatabase {
        return Room.databaseBuilder(
            application,
            PrayerTimesDatabase::class.java,
            "prayer_times.db"
        ).build()
    }

    @Provides
    fun providePrayerTimesDao(database: PrayerTimesDatabase): PrayerTimesDao {
        return database.prayerTimesDao()
    }

    @Provides
    @Singleton
    fun providePrayerRepository(
        prayerTimesAPI: PrayerTimesAPI,
        prayerTimesDao: PrayerTimesDao
    ): PrayerRepository {
        return PrayerRepository(prayerTimesAPI, prayerTimesDao)
    }
}

