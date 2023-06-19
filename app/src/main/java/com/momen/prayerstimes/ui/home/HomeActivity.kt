package com.momen.prayerstimes.ui.home


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.momen.prayerstimes.data.model.PrayerTimes
import com.momen.prayerstimes.databinding.ActivityHomeBinding
import com.momen.prayerstimes.ui.qibla.MapsActivity
import com.momen.prayerstimes.utils.ViewState
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var prayerTimesAdapter: PrayerTimesAdapter
    private lateinit var countdownTimer: CountDownTimer
    private val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var index = day - 1
    private var prayerTimesList: List<PrayerTimes> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prayerTimesAdapter = PrayerTimesAdapter(this, ArrayList())
        binding.listViewPrayerTimes.adapter = prayerTimesAdapter
        observePrayerTimes()
        handleClicks()
        callApi()
        binding.swipeRefresher.setOnRefreshListener { callApi() }
        binding.buttonQibla.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun callApi() {
        if (checkLocationPermission()) {
            fetchUserLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun handleClicks() {
        binding.apply {
            buttonPrevious.setOnClickListener {
                if (index - 1 >= day - 1 && prayerTimesList.isNotEmpty()) {
                    val formattedPrayerTimes = formatPrayerTimes(prayerTimesList[--index])
                    binding.textViewDate.text = prayerTimesList[index].date.readable
                    prayerTimesAdapter.clear()
                    prayerTimesAdapter.addAll(formattedPrayerTimes)
                    prayerTimesAdapter.notifyDataSetChanged()
                }
            }
            buttonNext.setOnClickListener {
                if (index + 1 <= prayerTimesList.lastIndex && prayerTimesList.isNotEmpty()) {
                    val formattedPrayerTimes = formatPrayerTimes(prayerTimesList[++index])
                    binding.textViewDate.text = prayerTimesList[index].date.readable
                    prayerTimesAdapter.clear()
                    prayerTimesAdapter.addAll(formattedPrayerTimes)
                    prayerTimesAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun observePrayerTimes() {
        viewModel.viewState.observe(this) {
            binding.swipeRefresher.isRefreshing = false
            when (it) {
                is ViewState.Error -> Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_SHORT).show()
                is ViewState.Loading -> binding.swipeRefresher.isRefreshing = true
                is ViewState.Success -> {
                    prayerTimesList = it.prayerTimes
                    val formattedPrayerTimes = formatPrayerTimes(prayerTimesList[index])
                    binding.textViewDate.text = prayerTimesList[index].date.readable
                    prayerTimesAdapter.clear()
                    prayerTimesAdapter.addAll(formattedPrayerTimes)
                    prayerTimesAdapter.notifyDataSetChanged()
                    // Start the countdown timer for the next prayer time
                    val nextPrayerTime = getNextPrayerTime(formattedPrayerTimes)
                    if (nextPrayerTime != null) {
                        startCountdownTimer(nextPrayerTime)
                    }
                }
            }
        }
    }

    private fun getNextPrayerTime(prayerTimes: List<Pair<String, String>>): Date? {
        val currentDate = Calendar.getInstance().time
        for (prayerTime in prayerTimes) {
            val timeStr = prayerTime.second
            val prayerTimeWithDateStr = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(currentDate) + " " + timeStr
            val prayerTimeWithDate =
                SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).parse(
                    prayerTimeWithDateStr
                )
            if (prayerTimeWithDate != null && prayerTimeWithDate.after(currentDate)) {
                binding.textViewNextPrayer.text = prayerTime.first
                return prayerTimeWithDate
            }
        }
        return null
    }

    private fun startCountdownTimer(nextPrayerTime: Date) {
        val currentTime = Calendar.getInstance().time
        val timeDifference = nextPrayerTime.time - currentTime.time

        countdownTimer = object : CountDownTimer(timeDifference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000 % 60
                val minutes = millisUntilFinished / (1000 * 60) % 60
                val hours = millisUntilFinished / (1000 * 60 * 60) % 24

                val countdownText = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d", hours, minutes, seconds
                )
                binding.textViewNextPrayerCountdown.text = countdownText
            }

            override fun onFinish() {
                // Timer has finished, perform any required actions here
            }
        }

        countdownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }

    private fun formatPrayerTimes(prayerTimes: PrayerTimes): List<Pair<String, String>> {
        val formattedList = mutableListOf<Pair<String, String>>()

        val timings = prayerTimes.timings

        timings.let {
            formattedList.add(Pair("Fajr", extractTime(it.Fajr)))
            formattedList.add(Pair("Sunrise", extractTime(it.Sunrise)))
            formattedList.add(Pair("Dhuhr", extractTime(it.Dhuhr)))
            formattedList.add(Pair("Asr", extractTime(it.Asr)))
            formattedList.add(Pair("Maghrib", extractTime(it.Maghrib)))
            formattedList.add(Pair("Isha", extractTime(it.Isha)))
        }

        return formattedList
    }

    private fun extractTime(time: String): String {
        val pattern = "HH:mm"
        val timeWithoutTimeZone =
            time.substring(0, 5) // Extract only the time part without the timezone
        val parsedTime = SimpleDateFormat(pattern, Locale.getDefault()).parse(timeWithoutTimeZone)

        // Format the parsed time to the desired format
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(parsedTime)
    }

    private fun fetchPrayerTimes(latitude: Double, longitude: Double) {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1


        viewModel.getPrayerTimes(latitude, longitude, year, month)
    }

    private fun checkLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun fetchUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    try {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        fetchPrayerTimes(latitude, longitude)
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            binding.textViewAddress.text =
                                "${address.subAdminArea}, ${address.adminArea} ${address.countryName}"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
