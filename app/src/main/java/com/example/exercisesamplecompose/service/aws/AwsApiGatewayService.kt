package com.example.exercisesamplecompose.service.aws

import com.example.exercisesamplecompose.pojo.WorkoutDetailsDto

interface AwsApiGatewayService {
    suspend fun getWorkoutDetails(workoutId: String): WorkoutDetailsDto
}