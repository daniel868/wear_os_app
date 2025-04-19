package com.example.exercisesamplecompose.presentation.workout.details

import com.example.exercisesamplecompose.pojo.WorkoutDetailsDto

sealed class WorkoutDetailsScreenState {
    object Loading : WorkoutDetailsScreenState()
    data class Success(val workoutDetails: WorkoutDetailsDto) : WorkoutDetailsScreenState()
    data class Error(val message: String) : WorkoutDetailsScreenState()
}