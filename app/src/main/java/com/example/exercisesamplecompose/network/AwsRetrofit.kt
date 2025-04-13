package com.example.exercisesamplecompose.network

import com.example.exercisesamplecompose.pojo.WorkoutDetailsDto
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://5iufu8cyc0.execute-api.us-east-1.amazonaws.com/V1/";

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(JacksonConverterFactory.create())
    .build()

interface AwsRetrofitApi {

    @GET("workoutDetails")
    suspend fun getWorkoutDetails(@Query("workoutId") workoutId: String): WorkoutDetailsDto
}

object AwsRetrofitApiProvider {
    val awsRetrofitApi: AwsRetrofitApi by lazy {
        retrofit.create(AwsRetrofitApi::class.java)
    }
}