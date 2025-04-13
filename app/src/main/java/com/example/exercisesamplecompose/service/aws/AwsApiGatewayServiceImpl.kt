package com.example.exercisesamplecompose.service.aws

import com.example.exercisesamplecompose.network.AwsRetrofitApi
import com.example.exercisesamplecompose.pojo.WorkoutDetailsDto
import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject

class AwsApiGatewayServiceImpl @Inject constructor(
    private val awsRetrofitApi: AwsRetrofitApi
) : AwsApiGatewayService {
    override suspend fun getWorkoutDetails(workoutId: String): WorkoutDetailsDto {
        return awsRetrofitApi.getWorkoutDetails(workoutId)
    }
}