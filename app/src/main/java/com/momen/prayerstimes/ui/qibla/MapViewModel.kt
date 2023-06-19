package com.momen.prayerstimes.ui.qibla

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momen.prayerstimes.data.model.QiblaDirectionData
import com.momen.prayerstimes.data.repository.QiblaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val qiblaRepository: QiblaRepository) : ViewModel() {

    private val _qiblaDirection = MutableLiveData<QiblaDirectionData>()
    val qiblaDirection: LiveData<QiblaDirectionData> = _qiblaDirection

    fun fetchQiblaDirection(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val direction = qiblaRepository.getQiblaDirection(latitude, longitude)
                _qiblaDirection.value = direction
            } catch (e: Exception) {
                // Handle error case
                Log.e("MapViewModel", "Error fetching Qibla direction: ${e.message}")
            }
        }
    }
}


