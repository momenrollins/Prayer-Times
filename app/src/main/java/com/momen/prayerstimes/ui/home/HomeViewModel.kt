package com.momen.prayerstimes.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momen.prayerstimes.utils.ViewState
import com.momen.prayerstimes.data.repository.PrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val prayerRepository: PrayerRepository) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    fun getPrayerTimes(latitude: Double, longitude: Double, year: Int, month: Int) {
        viewModelScope.launch {
            try {
                _viewState.value = ViewState.Loading

                val prayerTimes = prayerRepository.getPrayerTimes(latitude, longitude, year, month)
                _viewState.value = ViewState.Success(prayerTimes)
            } catch (e: Exception) {
                _viewState.value = ViewState.Error("Error fetching prayer times: ${e.message}")
            }
        }
    }
}