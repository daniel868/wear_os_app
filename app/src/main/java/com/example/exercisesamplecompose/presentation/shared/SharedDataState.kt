package com.example.exercisesamplecompose.presentation.shared

data class SharedDataState(
    val selectedWorkoutExercise: String?,
    val isGpsRequired: Boolean?,
    val trackingDataTypes: List<String> = listOf()
)
