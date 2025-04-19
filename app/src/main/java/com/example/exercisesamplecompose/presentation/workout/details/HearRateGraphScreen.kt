package com.example.exercisesamplecompose.presentation.workout.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices


@Composable
fun HeartRateTimeGraphWithTimestamp(
    heartRates: List<Int>
) {
    val graphColor = Color.Red
    val axisColor = Color.Gray
    val textMeasurer = rememberTextMeasurer()
    val textColor = MaterialTheme.colors.onSurface

    val timeStampsInMinutes = ArrayList<Int>();

    for (i in heartRates.indices) {
        timeStampsInMinutes.add(i + 1)
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
            .padding(8.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val padding = 40f
        val primaryColor = Color(0xFF4285F4)
        val gridColor = Color.LightGray.copy(alpha = 0.3f)

        // Find min/max values
        val maxHeartRate = heartRates.maxOrNull()?: 200
        val minHeartRate = heartRates.minOrNull()?: 40
        val maxTime = timeStampsInMinutes.maxOrNull() ?: 10
        val yRange = (maxHeartRate - minHeartRate).coerceAtLeast(1)

        // Draw Y axis
        drawLine(
            color = primaryColor.copy(alpha = 0.8f),
            start = Offset(padding, padding),
            end = Offset(padding, canvasHeight - padding),
            strokeWidth = 2.dp.toPx()
        )

        // Draw X axis
        drawLine(
            color = primaryColor.copy(alpha = 0.8f),
            start = Offset(padding, canvasHeight - padding),
            end = Offset(canvasWidth - padding, canvasHeight - padding),
            strokeWidth = 2.dp.toPx()
        )

        // horizontal grid lines
        val ySteps = 5
        repeat(ySteps + 1) { i ->
            val yPos = padding + i * (canvasHeight - 2 * padding) / ySteps
            drawLine(
                color = gridColor,
                start = Offset(padding, yPos),
                end = Offset(canvasWidth - padding, yPos),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
            )
        }

        // Vertical grid lines
        val xSteps = maxTime.coerceAtMost(10)
        repeat(xSteps + 1) { i ->
            val xPos = padding + i * (canvasWidth - 2 * padding) / xSteps
            drawLine(
                color = gridColor,
                start = Offset(xPos, padding),
                end = Offset(xPos, canvasHeight - padding),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
            )
        }


        // Y-axis labels (BPM)
        repeat(ySteps + 1) { i ->
            val yPos = canvasHeight - padding - i * (canvasHeight - 2 * padding) / ySteps
            val value = minHeartRate + (yRange * i / ySteps)

            drawText(
                textMeasurer = textMeasurer,
                text = "$value BPM",
                style = TextStyle(
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 10.sp
                ),
                topLeft = Offset(0f, yPos - 10f)
            )
        }

        val totalDuration = maxTime // e.g., 60 minutes
        val labelInterval = when {
            totalDuration <= 10 -> 1       // Show every minute if <10min
            totalDuration <= 30 -> 5       // Show every 5min if <30min
            else -> 10                     // Show every 10min otherwise
        }

        // X-axis labels (BPM)
        for (i in 0..totalDuration step labelInterval) {
            val xPos = padding + (i.toFloat() / totalDuration) * (canvasWidth - 2 * padding)

            val xLineText = if (i + labelInterval > (totalDuration)) "$i min" else i

            drawText(
                textMeasurer = textMeasurer,
                text = "$xLineText",
                style = TextStyle(
                    color = textColor,
                    fontSize = 10.sp,
                ),
                topLeft = Offset(xPos - 15f, canvasHeight - padding + 15f)
            )
        }


        // Draw heart rate line
        val xScale = (canvasWidth - 2 * padding) / maxTime
        val yScale = (canvasHeight - 2 * padding) / yRange

        for (i in 0 until heartRates.size - 1) {
            val startX = padding + timeStampsInMinutes[i] * xScale
            val startY = canvasHeight - padding - (heartRates[i] - minHeartRate) * yScale

            val endX = padding + timeStampsInMinutes[i+1] * xScale
            val endY = canvasHeight - padding - (heartRates[i+1] - minHeartRate) * yScale

            drawLine(
                color = graphColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 3f
            )

            // Draw data points
            drawCircle(
                color = graphColor,
                radius = 4f,
                center = Offset(startX, startY)
            )
        }

        // Draw last data point
        if (heartRates.isNotEmpty()) {
            val lastX = padding + timeStampsInMinutes.last() * xScale
            val lastY = canvasHeight - padding - (heartRates.last() - minHeartRate) * yScale
            drawCircle(
                color = graphColor,
                radius = 4f,
                center = Offset(lastX, lastY)
            )
        }
    }
}


@WearPreviewDevices
@Composable
fun PreviewHeartRateGraphScreen(){
    val heartRates = listOf(
        80, 85, 90, 95, 100,105, 110,115, 120,120, 125, 130, 85, 100, 120, 140,
        105, 110,115, 120,120, 125, 130, 85, 100, 120, 140,
        130, 110, 115, 130, 120, 140, 100,
        80, 85, 90, 95, 100,105, 110,115, 120,120, 125, 130, 85, 100, 120, 140,
        105, 110,115, 120,120, 125, 130, 85, 100, 120, 140,
        130, 110, 115, 130, 120, 140, 100
    )
//    120, 125, 130, 85, 100, 120, 140,
//    130, 110, 115, 130, 120, 140, 100
    HeartRateTimeGraphWithTimestamp(heartRates)
}