package com.example.weatherdata.domain.repository

import com.example.weatherdata.domain.model.SensorReading

interface SensorRepository {
    fun getLatestReading(onSuccess: (SensorReading?) -> Unit, onError: (String) -> Unit)
    fun listenForLatestReading(onNewData: (SensorReading?) -> Unit, onError: (String) -> Unit): Any
    fun getReadingsInLastHour(onSuccess: (List<SensorReading>) -> Unit, onError: (String) -> Unit)
    fun removeListener(listener: Any)
}