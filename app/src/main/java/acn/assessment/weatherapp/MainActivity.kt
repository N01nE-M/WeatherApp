package acn.assessment.weatherapp

import acn.assessment.weatherapp.LocationPermissionDetails
import acn.assessment.weatherapp.LocationPermissionNotAvailable
import acn.assessment.weatherapp.R
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import acn.assessment.weatherapp.service.data.dto.CurrentWeather
import acn.assessment.weatherapp.service.data.dto.FullWeather
import acn.assessment.weatherapp.ui.theme.ThemeState
import acn.assessment.weatherapp.ui.theme.WeatherAppTheme
import acn.assessment.weatherapp.viewmodels.MainViewModel
import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt
import androidx.compose.runtime.Composable


@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                val permission =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

                PermissionRequired(
                    permissionState = permission,
                    permissionNotGrantedContent = { LocationPermissionDetails(onContinueClick = permission::launchPermissionRequest) },
                    permissionNotAvailableContent = { LocationPermissionNotAvailable(onContinueClick = permission::launchPermissionRequest) }

                )
                {
                    Surface(color = MaterialTheme.colors.primary) {

                    }
                    MainScreen(viewModel)
                }

            }
        }

    }

}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val current by viewModel.current.collectAsState(null)
    val forecast by viewModel.forecast.collectAsState(emptyList())
    current?.let {
        rememberSystemUiController().setStatusBarColor(it.backgroundColour())
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(current?.backgroundColour() ?: Color.White)
    ) {
        current?.let {
            WeatherSummary(weather = it)
            TemperatureSummary(it)
            Divider(color = Color.White)
        }
        FiveDayForecast(forecast)
        Box(
            modifier = Modifier
                .fillMaxSize()
        )

    }
}

@Composable
fun WeatherSummary(weather: CurrentWeather) {

    Surface {
        Box {

            Image(
                painter = painterResource(id = weather.background()),
                contentDescription = "Background",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
            Column(
                Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { ThemeState.isLight = !ThemeState.isLight }) {
                    if (ThemeState.isLight) {
                        Text(text = "Dark Theme")
                    } else {
                        Text(text = "Light Theme")
                    }
                }
            }

            Column(
                Modifier
                    .padding(top = 100.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = formatTemperature(weather.main.temp), fontSize = 50.sp)
                Text(text = weather.weather.first().main, fontSize = 30.sp)
                Text(text = weather.name, fontSize = 20.sp)
                Text(text = weather.sys.country, fontSize = 18.sp)
                Button(
                    onClick = { },
                    modifier = Modifier.padding(horizontal = 40.dp, vertical = 30.dp)
                )
                {
                    Text(text = "Search Location")
                }

            }
        }
    }
}

@Composable
fun TemperatureSummary(weather: CurrentWeather) {
    Surface {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTemperature(weather.main.tempMin),
                    fontSize = 18.sp,
                )
                Text(text = stringResource(R.string.min_temperature))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTemperature(weather.main.tempMax),
                    fontSize = 18.sp,
                )
                Text(text = stringResource(R.string.max_temperature))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = weather.main.humidity.toString() + "%",
                    fontSize = 18.sp,
                )
                Text(text = stringResource(R.string.humidity))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = weather.main.pressure.toString(),
                    fontSize = 18.sp,
                )
                Text(text = stringResource(R.string.pressure))
            }
        }
    }
}

@Composable
fun FiveDayForecast(forecast: List<FullWeather.Daily>) {
    Surface {
        LazyColumn {
            items(forecast) { dayForecast ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("EEEE").format(Date(dayForecast.dt * 1_000)),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Image(
                        painter = painterResource(dayForecast.forecastIcon()),
                        contentDescription = "Forecast icon",
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)

                    )
                    Text(
                        text = dayForecast.humidity.toString() + "%",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 65.dp)
                    )
                    Text(
                        text = formatTemperature(dayForecast.temp.day),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )

                }
            }
        }
    }
}

@Composable
private fun formatTemperature(temperature: Double): String {
    return stringResource(R.string.temperature_degrees, temperature.roundToInt())
}

@DrawableRes
private fun CurrentWeather.background(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.cloudy
        conditions.contains("rain", ignoreCase = true) -> R.drawable.rainy
        conditions.contains("thunderstorm", ignoreCase = true) -> R.drawable.rainy
        else -> R.drawable.sunny
    }
}

@Composable
private fun CurrentWeather.backgroundColour(): Color {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> MaterialTheme.colors.background
        conditions.contains("rain", ignoreCase = true) -> MaterialTheme.colors.background
        else -> MaterialTheme.colors.background
    }
}

@Composable
private fun FullWeather.Daily.forecastIcon(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.partlysunny
        conditions.contains("rain", ignoreCase = true) -> R.drawable.rain
        else -> R.drawable.clear
    }
}