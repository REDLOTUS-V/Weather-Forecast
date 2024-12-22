package com.project.weatherapp.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.weatherapp.R
import com.project.weatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel, navController: NavController) {
    val weatherState by viewModel.weatherState.collectAsState()
    val cityInput by viewModel.cityInput.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // City Input TextField
        TextField(
            value = cityInput,
            onValueChange = viewModel::onCityInputChange,
            label = { Text("Enter City Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface,
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )


        // Weather Button
        Button(
            onClick = { viewModel.fetchWeather() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Weather")
        }

        // Current Weather Card
        Card(
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Current Weather",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    weatherState.isLoading -> CircularProgressIndicator()
                    weatherState.error != null -> Text(
                        text = weatherState.error.orEmpty(),
                        color = MaterialTheme.colors.error
                    )
                    else -> {
                        Text("City: ${weatherState.cityName}")
                        Text("Temperature: ${weatherState.temperature}")
                        Text("Condition: ${weatherState.condition}")
                        Text("Humidity: ${weatherState.humidity}")
                        Text("Wind Speed: ${weatherState.windSpeed}")
                    }
                }
            }
        }

        // Forecast Card
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("forecast") }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Forecast",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Next 5 day ",
                    color = MaterialTheme.colors.primary,
                    fontSize = 16.sp
                )
            }
        }
    }
}
