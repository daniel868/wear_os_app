package com.example.exercisesamplecompose.pojo.model

import com.example.exercisesamplecompose.pojo.HeartRateDto
import java.time.LocalDateTime

data class WorkoutModelInfo(
    val id: String,
    val type: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    )

data class WorkoutMetrics(
    val supportGpsTrack: Boolean,
    val totalCaloriesBurned: Int,
    val heartRate: HeartRateDto,
    val restingExerciseDuration: Long,
    val totalDistance: Double,
    val averagePaceStatus: Double
)


data class PaceStatus(
    val min: Double,
    val max: Double,
    val average: Double
)