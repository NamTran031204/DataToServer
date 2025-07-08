package com.example.weatherdata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherdata.data.remote.FirebaseDataService
import com.example.weatherdata.data.repository.SensorRepositoryImpl
import com.example.weatherdata.presentation.ui.SensorDataScreen
import com.example.weatherdata.presentation.ui.theme.ESP32WeatherTheme
import com.example.weatherdata.presentation.viewmodel.SensorViewModel
import com.example.weatherdata.presentation.viewmodel.SensorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo repository và viewModel
        val firebaseService = FirebaseDataService()
        val repository = SensorRepositoryImpl(firebaseService)
        val viewModelFactory = SensorViewModelFactory(repository)

        setContent {
            ESP32WeatherTheme {
                // Cung cấp ViewModel với Factory
                val viewModel: SensorViewModel = viewModel(factory = viewModelFactory)

                // Hiển thị màn hình chính
                SensorDataScreen(viewModel)
            }
        }
    }
}