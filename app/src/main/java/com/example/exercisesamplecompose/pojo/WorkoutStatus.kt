package com.example.exercisesamplecompose.pojo

import java.util.Date

data class WorkoutStatus(
    val id: String,
    val workoutType: String,
    val startTime: Date?,
    val endTime: Date?
)
