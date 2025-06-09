package com.example.exercisesamplecompose.pojo

data class WorkoutInfo(
    val workoutType: String,
    val supportedFeature: List<String>,
    val isGpsRequired: Boolean = false
)
