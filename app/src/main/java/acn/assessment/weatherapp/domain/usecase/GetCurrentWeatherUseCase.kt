package acn.assessment.weatherapp.domain.usecase

import acn.assessment.weatherapp.datasource.repository.WeatherRepository
import android.annotation.SuppressLint
import javax.inject.Inject

@SuppressLint("MissingPermission")
class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
)  {
     operator fun invoke() = repository.getCurrentWeather()
}

