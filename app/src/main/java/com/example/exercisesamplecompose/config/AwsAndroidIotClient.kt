package com.example.exercisesamplecompose.config

import com.common.exercise.aws.iot.AwsIotServiceClient

object AwsAndroidIotClient {

    val serviceClient: AwsIotServiceClient by lazy {
        AwsIotServiceClient()
    }
}
