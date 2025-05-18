package com.example.weatherdata.data.repository

import com.example.weatherdata.data.remote.FirebaseDataService
import com.example.weatherdata.domain.model.SensorReading
import com.example.weatherdata.domain.repository.SensorRepository
import com.google.firebase.database.ValueEventListener

class SensorRepositoryImpl(private val firebaseService: FirebaseDataService) : SensorRepository {
    override fun getLatestReading(onSuccess: (SensorReading?) -> Unit, onError: (String) -> Unit) {
        firebaseService.getLatestReading(onSuccess, onError)
    }

    override fun listenForLatestReading(onNewData: (SensorReading?) -> Unit, onError: (String) -> Unit): Any {
        return firebaseService.listenForLatestReading(onNewData, onError)
    }

    override fun getReadingsInLastHour(onSuccess: (List<SensorReading>) -> Unit, onError: (String) -> Unit) {
        firebaseService.getReadingsInLastHour(onSuccess, onError)
    }

    override fun removeListener(listener: Any) {
        if (listener is ValueEventListener) {
            firebaseService.removeListener(listener)
        }
    }
}