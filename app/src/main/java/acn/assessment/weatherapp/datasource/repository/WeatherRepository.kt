package acn.assessment.weatherapp.datasource.repository

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import acn.assessment.weatherapp.BuildConfig
import acn.assessment.weatherapp.datasource.api.OpenWeatherService
import acn.assessment.weatherapp.datasource.remotemodel.CurrentWeather
import acn.assessment.weatherapp.datasource.remotemodel.FullWeather
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val service: OpenWeatherService,
    private val application: Application
) {

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getCurrentWeather(): Flow<CurrentWeather> {
        return locationFlow().map {
            service.getCurrentWeather(it.latitude, it.longitude, BuildConfig.API_KEY)
                .body()
        }.filterNotNull()
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun getFiveDayForecast(): Flow<List<FullWeather.Daily>> {
        return locationFlow().map {
            service.getFullWeather(it.latitude, it.longitude, BuildConfig.API_KEY)
                .body()
        }.map {
            it?.daily?.drop(1)?.take(5)
        }.filterNotNull()
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow() = channelFlow<Location> {
        val client = LocationServices.getFusedLocationProviderClient(application)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }
        val request = LocationRequest.create()
            .setInterval(10_000)
            .setFastestInterval(5_000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(170f)

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}