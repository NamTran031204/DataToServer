package com.example.weatherdata.domain.model

data class SensorDisplayData(
    val currentTemperature: Float,
    val currentHumidity: Float,
    val averageTemperature: Float,
    val averageHumidity: Float
)