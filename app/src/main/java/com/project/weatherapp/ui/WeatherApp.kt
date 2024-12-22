package com.project.weatherapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.weatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherApp(viewModel: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController,
             startDestination = "weather") {
        composable("weather") {
            WeatherScreen(viewModel, navController)
        }
        composable("forecast") {
            ForecastScreen(viewModel, navController)
        }
    }
}
