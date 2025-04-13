package com.example.exercisesamplecompose.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.health.services.client.data.LocationData
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun StepPerMinuteText(stepsPerMinute: String) {
    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stepsPerMinute.toString(), fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 20.sp
            )
            Text(
                text = "steps/minute", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LocationText(location: LocationData?) {
    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = location?.latitude.let { "${"%.2f".format(it)}°" },
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 20.sp
            )
            Text(
                text = "Altitude", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = location?.longitude.let { "${"%.2f".format(it)}°" },
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 20.sp
            )
            Text(
                text = "Longitude", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }

    }
}