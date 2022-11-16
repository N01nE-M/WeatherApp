package acn.assessment.weatherapp.domain.usecase

import acn.assessment.weatherapp.datasource.model.CurrentWeather
import acn.assessment.weatherapp.datasource.repository.WeatherRepository
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@SuppressLint("MissingPermission")
class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    val currentWeather: Flow<CurrentWeather> = repository.getCurrentWeather()
}

