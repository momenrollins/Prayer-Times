package com.momen.prayerstimes.utils

import com.momen.prayerstimes.data.model.PrayerTimes

sealed class ViewState {
    object Loading : ViewState()
    data class Success(val prayerTimes: List<PrayerTimes>) : ViewState()
    data class Error(val errorMessage: String) : ViewState()
}