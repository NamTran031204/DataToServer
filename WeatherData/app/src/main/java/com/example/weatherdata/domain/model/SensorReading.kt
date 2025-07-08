package com.example.weatherdata.domain.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SensorReading(
    val id: String? = null,
    val temperature: Float? = null,
    val humidity: Float? = null,
    val deviceId: String? = null,
    val timestamp: String? = null
)