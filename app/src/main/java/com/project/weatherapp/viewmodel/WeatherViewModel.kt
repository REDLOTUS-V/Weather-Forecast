package com.project.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weatherapp.network.WeatherService
import com.project.weatherapp.network.ForecastItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WeatherState(
    val cityName: String = "",
    val temperature: String = "",
    val condition: String = "",
    val humidity: String = "",
    val windSpeed: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ForecastState(
    val forecastList: List<ForecastItem>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {

    private val weatherService = WeatherService.create()

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState

    private val _forecastState = MutableStateFlow(ForecastState())
    val forecastState: StateFlow<ForecastState> = _forecastState

    private val _cityInput = MutableStateFlow("")
    val cityInput: StateFlow<String> = _cityInput

    private val apiKey = "be3fd959ddd32f037be7ca827a835361"

    fun onCityInputChange(newCity: String) {
        _cityInput.value = newCity
    }

    fun fetchWeather() {
        val city = _cityInput.value.trim()
        if (city.isEmpty()) {
            _weatherState.value = _weatherState.value.copy(error = "City name cannot be empty")
            return
        }

        viewModelScope.launch {
            _weatherState.value = _weatherState.value.copy(isLoading = true)
            try {
                val response = weatherService.getWeather(city, apiKey)
                _weatherState.value = WeatherState(
                    cityName = response.name,
                    temperature = "${response.main.temp}°C",
                    condition = response.weather.firstOrNull()?.description ?: "N/A",
                    humidity = "${response.main.humidity}%",
                    windSpeed = "${response.wind.speed} km/s",
                    isLoading = false
                )
            } catch (e: Exception) {
                _weatherState.value = _weatherState.value.copy(
                    error = "Failed to fetch weather data",
                    isLoading = false
                )
            }
        }
    }

    fun fetchForecast() {
        val city = _cityInput.value.trim()
        if (city.isEmpty()) {
            _forecastState.value = _forecastState.value.copy(error = "City name cannot be empty")
            return
        }

        viewModelScope.launch {
            _forecastState.value = _forecastState.value.copy(isLoading = true)
            try {
                val response = weatherService.getForecast(city, apiKey)
                _forecastState.value = ForecastState(
                    forecastList = response.list,
                    isLoading = false
                )
            } catch (e: Exception) {
                _forecastState.value = _forecastState.value.copy(
                    error = "Failed to fetch forecast data",
                    isLoading = false
                )
            }
        }
    }
}