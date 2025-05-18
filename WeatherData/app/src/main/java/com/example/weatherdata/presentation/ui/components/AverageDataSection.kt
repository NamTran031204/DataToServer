package com.example.weatherdata.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherdata.R
import com.example.weatherdata.presentation.ui.theme.AppFonts
import com.example.weatherdata.presentation.ui.theme.White

@Composable
fun AverageDataSection(averageTemp: Float, averageHumid: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "average (1 hour)",
            fontFamily = AppFonts.montserratBlack,
            fontSize = 24.sp,
            color = White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Temperature average row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.temperature),
                contentDescription = "Temperature Icon",
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Temp",
                fontFamily = AppFonts.montserratBlack,
                fontSize = 25.sp,
                color = White
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = String.format("%.1f", averageTemp) + "°C",
                fontFamily = AppFonts.montserratSemiBold,
                fontSize = 35.sp,
                color = White
            )
        }

        // Humidity average row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.water_drop),
                contentDescription = "Humidity Icon",
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Humid",
                fontFamily = AppFonts.montserratBlack,
                fontSize = 25.sp,
                color = White
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = String.format("%.1f", averageHumid) + "%",
                fontFamily = AppFonts.montserratSemiBold,
                fontSize = 35.sp,
                color = White
            )
        }
    }
}