package com.example.weatherdata.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherdata.presentation.ui.components.DataDisplayCard
import com.example.weatherdata.presentation.ui.components.Header
import com.example.weatherdata.presentation.ui.theme.AppFonts
import com.example.weatherdata.presentation.ui.theme.DarkBlue
import com.example.weatherdata.presentation.ui.theme.LightBlue
import com.example.weatherdata.presentation.ui.theme.White
import com.example.weatherdata.presentation.viewmodel.SensorViewModel

@Composable
fun SensorDataScreen(viewModel: SensorViewModel = viewModel()) {
    val sensorData by viewModel.sensorData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        LightBlue,  // #5de0e6
                        DarkBlue    // #004aad
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with cloud icon and menu
            Header()

            Spacer(modifier = Modifier.height(32.dp))

            // Data display card
            sensorData?.let { data ->
                DataDisplayCard(data)
            } ?: run {
                // Loading state
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Loading data...",
                    color = White,
                    fontFamily = AppFonts.montserratSemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Error message if any
            errorMessage?.let {
                Text(
                    text = it,
                    color = White,
                    fontFamily = AppFonts.montserratSemiBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}