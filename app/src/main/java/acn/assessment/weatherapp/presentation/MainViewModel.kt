package acn.assessment.weatherapp.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import acn.assessment.weatherapp.datasource.repository.WeatherRepository
import acn.assessment.weatherapp.datasource.remotemodel.CurrentWeather
import acn.assessment.weatherapp.datasource.remotemodel.FullWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class MainViewModel @Inject constructor(
    repository: WeatherRepository
) : ViewModel() {

    val current: Flow<CurrentWeather> = repository.getCurrentWeather()
    val forecast: Flow<List<FullWeather.Daily>> = repository.getFiveDayForecast()

}