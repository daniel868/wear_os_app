package com.example.exercisesamplecompose.presentation.workout.selector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.exercisesamplecompose.pojo.WorkoutInfo
import com.example.exercisesamplecompose.presentation.shared.SharedViewModel

@Composable
fun WorkoutSelectorRoute(
    onWorkoutItemClick: () -> Unit
) {
    //TODO: fetch this workouts from the local DB cache thorough view-model
    val workouts = listOf(
        WorkoutInfo(
            "WALKING",
            listOf(
                "Distance",
                "Steps",
                "Location",
                "Calories",
                "HeartRate",
                "Location",
                "Step per minute"
            ),
            isGpsRequired = true
        ),
        WorkoutInfo(
            "RUNNING",
            listOf(
                "Distance",
                "Pace",
                "Steps",
                "Calories",
                "HeartRate",
                "Location",
                "Step per minute"
            ),
            isGpsRequired = true
        ),
        WorkoutInfo(
            "SWIMMING_POOL",
            listOf(
                "Distance",
                "Swim Lap Count",
                "Calories",
                "HeartRate"
            )
        ),
    )
    val sharedViewModel = hiltViewModel<SharedViewModel>()

    WorkoutSelectorScreen(
        workouts = workouts,
        updateWorkoutType = {
            sharedViewModel.updateWorkoutType(it)
        },
        onWorkoutItemClick = onWorkoutItemClick
    )
}

@Composable
fun WorkoutSelectorScreen(
    workouts: List<WorkoutInfo>,
    updateWorkoutType: (workoutInfo: WorkoutInfo) -> Unit,
    onWorkoutItemClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(workouts) { workout ->
            WorkoutItem(workout.workoutType) {
                onWorkoutItemClick()
                updateWorkoutType(workout)
            }
        }
    }
}


@Composable
@WearPreviewDevices
fun WorkoutSelectorScreenPreview() {
    val workouts = listOf(
        WorkoutInfo("WALKING", listOf()),
        WorkoutInfo("RUNNING", listOf()),
        WorkoutInfo("VOLLEYBALL", listOf()),
        WorkoutInfo("WATER_POLO", listOf())
    )
    WorkoutSelectorScreen(workouts, updateWorkoutType = {}) {}
}