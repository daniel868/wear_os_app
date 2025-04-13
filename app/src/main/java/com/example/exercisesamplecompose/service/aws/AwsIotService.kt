package com.example.exercisesamplecompose.service.aws

import com.example.exercisesamplecompose.config.AwsAndroidIotClient
import com.example.exercisesamplecompose.service.ExerciseLogger

class AwsIotService(
    val logger: ExerciseLogger
) {
    suspend fun <T> publishDataToIotCore(payload: T, topic: String) {
        logger.log("Publishing to IOT Core: Topic: $topic and payload: $payload")
        AwsAndroidIotClient.serviceClient.publishDataToIotCore(payload, topic)
    }

}