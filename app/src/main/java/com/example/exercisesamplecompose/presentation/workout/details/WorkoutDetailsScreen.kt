package com.example.exercisesamplecompose.presentation.workout.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.exercisesamplecompose.R
import com.example.exercisesamplecompose.pojo.HeartRateDto
import com.example.exercisesamplecompose.pojo.LocationDataDto
import com.example.exercisesamplecompose.pojo.WorkoutDetailsDto
import com.example.exercisesamplecompose.presentation.theme.ThemePreview
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutDetailsScreenRoute() {
    val viewModel = hiltViewModel<WorkoutDetailsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when(uiState){
            is WorkoutDetailsScreenState.Loading ->{
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    strokeWidth = 4.dp
                )
            }
            is WorkoutDetailsScreenState.Success -> {
                val workoutData: WorkoutDetailsDto = (uiState as WorkoutDetailsScreenState.Success).workoutDetails
                WorkoutDetailsScreen(workoutData)
            }

            is WorkoutDetailsScreenState.Error -> {
                val errorText = (uiState as WorkoutDetailsScreenState.Error).message
                Text(text =errorText)
            }
        }
    }


}

@Composable
fun WorkoutDetailsScreen(workoutData: WorkoutDetailsDto?) {
    var showHeartRateList by remember { mutableStateOf(false) }
    var showLocationList by remember { mutableStateOf(false) }

    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Chip, last = ItemType.Unspecified
        )
    )
    ScreenScaffold(scrollState = columnState, timeText = {}) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = "Workout Details", fontSize = 10.sp)
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = " ${formatTime(workoutData!!.startTime)} - ${formatTime(workoutData.finishTime)}",
                        fontSize = 12.sp
                    )
                }
            }

            item {
                Chip(
                    label = {
                        Column {
                            Text("Heart Rate", fontSize = 14.sp, color = Color.White)
                            Text("View heart rate info", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.heart_rate_bold),
                            contentDescription = "Heart Icon",
                            tint = Color.Red,
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                        .fillMaxSize(),
                    onClick = {
                        showHeartRateList = !showHeartRateList
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = Color.Transparent
                    ),
                    border = ChipDefaults.chipBorder(
                        borderStroke = BorderStroke(1.dp, Color.Gray)
                    )
                )
            }
            if (showHeartRateList) {
                item{
                    if (!workoutData!!.heartRateStatus.isEmpty()){
                        HeartRateCard(heartRate = workoutData.heartRateStatus[0])
                    }
                }
                item {
                    val heartRates:List<Int> = if (workoutData?.heartRateStatus == null) {
                        listOf()
                    } else{
                        workoutData.heartRateStatus.map {
                            it.average.toInt()
                        }
                    }
                    HeartRateTimeGraphWithTimestamp(heartRates)
                }
            }

            item {
                Chip(
                    label = {
                        Column {
                            Text("Location", fontSize = 14.sp, color = Color.White)
                            Text("View location info", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = "Location Icon",
                            tint = Color.Magenta,
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                    },
                    modifier = Modifier
                        .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                        .fillMaxSize(),
                    onClick = {
                        showLocationList = !showLocationList
                    },
                    colors = ChipDefaults.chipColors(
                        backgroundColor = Color.Transparent
                    ),
                    border = ChipDefaults.chipBorder(
                        borderStroke = BorderStroke(1.dp, Color.Gray)
                    )
                )
            }
            if (showLocationList) {
                items(workoutData!!.locations) { loc ->
                    LocationCard(loc)
                }
            }

        }
    }
}

@Composable
fun HeartRateCard(heartRate: HeartRateDto) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        backgroundPainter = CardDefaults.cardBackgroundPainter()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Spacer(modifier = Modifier.height(5.dp))
            Text("Avg: ${heartRate.average} BPM", fontSize = 10.sp)
            Text("Min: ${heartRate.min} BPM", fontSize = 10.sp)
            Text("Max: ${heartRate.max} BPM", fontSize = 10.sp)
        }
    }
}

@Composable
fun LocationCard(location: LocationDataDto) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        backgroundPainter = CardDefaults.cardBackgroundPainter()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Spacer(modifier = Modifier.height(5.dp))
            Text("Lat: ${location.latitude}", fontSize = 10.sp)
            Text("Lng: ${location.longitude}", fontSize = 10.sp)
        }
    }
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@WearPreviewDevices
@Composable
fun WorkoutDetailsScreenPreview() {
    val mockData: String = "{\n" +
            "    \"workoutType\": \"RUNNING\",\n" +
            "    \"startTime\": 1738423521295,\n" +
            "    \"finishTime\": 1738423771348,\n" +
            "    \"durationDto\": {\n" +
            "        \"hours\": 0,\n" +
            "        \"minutes\": 4,\n" +
            "        \"seconds\": 10\n" +
            "    },\n" +
            "    \"heartRateStatus\": [\n" +
            "        {\n" +
            "            \"average\": 102,\n" +
            "            \"min\": 60.0,\n" +
            "            \"max\": 150.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"average\": 104,\n" +
            "            \"min\": 60.0,\n" +
            "            \"max\": 150.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"average\": 103,\n" +
            "            \"min\": 60.0,\n" +
            "            \"max\": 150.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"average\": 103,\n" +
            "            \"min\": 60.0,\n" +
            "            \"max\": 150.0\n" +
            "        }\n" +
            "    ],\n" +
            "    \"locations\": [\n" +
            "        {\n" +
            "            \"latitude\": 37.4268,\n" +
            "            \"longitude\": -122.0795\n" +
            "        },\n" +
            "        {\n" +
            "            \"latitude\": 37.4242,\n" +
            "            \"longitude\": -122.0821\n" +
            "        },\n" +
            "        {\n" +
            "            \"latitude\": 37.4255,\n" +
            "            \"longitude\": -122.0808\n" +
            "        },\n" +
            "        {\n" +
            "            \"latitude\": 37.4229,\n" +
            "            \"longitude\": -122.0834\n" +
            "        }\n" +
            "    ]\n" +
            "}"
    val workoutData = Gson().fromJson(mockData, WorkoutDetailsDto::class.java)
    ThemePreview {
        WorkoutDetailsScreen(workoutData)
    }


}


