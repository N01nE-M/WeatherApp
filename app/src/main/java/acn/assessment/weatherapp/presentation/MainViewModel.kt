package acn.assessment.weatherapp.presentation

import acn.assessment.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import acn.assessment.weatherapp.domain.usecase.GetFiveDayForecastUseCase
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
        private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
        private val getFiveDayForecastUseCase: GetFiveDayForecastUseCase,

): ViewModel() {

       operator fun invoke() = getCurrentWeatherUseCase.getCurrent()
       val forecast = getFiveDayForecastUseCase.getFiveDay()

}