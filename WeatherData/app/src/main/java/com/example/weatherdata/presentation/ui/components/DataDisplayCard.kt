package com.example.weatherdata.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherdata.domain.model.SensorDisplayData
import com.example.weatherdata.presentation.ui.theme.AppFonts
import com.example.weatherdata.presentation.ui.theme.SemiTransparentWhite
import com.example.weatherdata.presentation.ui.theme.White

@Composable
fun DataDisplayCard(data: SensorDisplayData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = SemiTransparentWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current temperature and humidity row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Temperature column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Temp",
                        fontFamily = AppFonts.montserratBlack,
                        fontSize = 30.sp,
                        color = White
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = String.format("%.1f", data.currentTemperature),
                            fontFamily = AppFonts.montserratSemiBold,
                            fontSize = 50.sp,
                            color = White
                        )

                        Text(
                            text = "°C",
                            fontFamily = AppFonts.montserratSemiBold,
                            fontSize = 30.sp,
                            color = White,
                            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                        )
                    }
                }

                // Humidity column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Humid",
                        fontFamily = AppFonts.montserratBlack,
                        fontSize = 30.sp,
                        color = White
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = String.format("%.1f", data.currentHumidity),
                            fontFamily = AppFonts.montserratSemiBold,
                            fontSize = 50.sp,
                            color = White
                        )

                        Text(
                            text = "%",
                            fontFamily = AppFonts.montserratSemiBold,
                            fontSize = 30.sp,
                            color = White,
                            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Average data section
            AverageDataSection(
                averageTemp = data.averageTemperature,
                averageHumid = data.averageHumidity
            )
        }
    }
}