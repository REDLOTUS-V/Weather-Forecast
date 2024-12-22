package com.project.weatherapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.weatherapp.R
import com.project.weatherapp.network.ForecastItem
import com.project.weatherapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ForecastScreen(viewModel: WeatherViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.fetchForecast()

    }

    val forecastState by viewModel.forecastState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("5-Day Forecast") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { contentPadding -> // Pass the contentPadding parameter here
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding)) {

            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            when {
                forecastState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                forecastState.error != null -> {
                    Text(
                        text = forecastState.error ?: "Unknown error",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                forecastState.forecastList.isNullOrEmpty() -> {
                    Text(
                        text = "No forecast data available.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filteredForecast = forecastState.forecastList ?: emptyList()

                        val groupedForecast = filteredForecast.groupBy {
                            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                            sdf.format(Date(it.dt * 1000L))
                        }

                        val forecastTime = groupedForecast.flatMap { (date, forecasts) ->
                            forecasts.map { forecast ->
                                val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(forecast.dt * 1000L))
                                ForecastItemTime(date, time, forecast)
                            }
                        }

                        items(forecastTime) { forecastItem ->
                            ForecastItem(forecastItem)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(forecastItemWithTime: ForecastItemTime) {
    val date = forecastItemWithTime.date
    val time = forecastItemWithTime.time
    val forecast = forecastItemWithTime.forecast

    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$date at $time",
                style = MaterialTheme.typography.h6
            )
            Text("Temperature: ${forecast.main.temp}°C")
            Text("Condition: ${forecast.weather.firstOrNull()?.description ?: "N/A"}")
            Text("Wind Speed: ${forecast.wind.speed} m/s")
        }
    }
}

data class ForecastItemTime(
    val date: String,
    val time: String,
    val forecast: ForecastItem
)
