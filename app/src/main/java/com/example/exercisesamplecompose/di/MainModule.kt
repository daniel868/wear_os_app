package com.example.exercisesamplecompose.di

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import com.example.exercisesamplecompose.network.AwsRetrofitApi
import com.example.exercisesamplecompose.network.AwsRetrofitApiProvider
import com.example.exercisesamplecompose.service.AndroidLogExerciseLogger
import com.example.exercisesamplecompose.service.aws.AwsIotService
import com.example.exercisesamplecompose.service.ExerciseLogger
import com.example.exercisesamplecompose.service.aws.AwsApiGatewayService
import com.example.exercisesamplecompose.service.aws.AwsApiGatewayServiceImpl
import com.example.exercisesamplecompose.service.shared.SharedService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Singleton
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Singleton
    @Provides
    fun provideHealthServicesClient(@ApplicationContext context: Context): HealthServicesClient =
        HealthServices.getClient(context)

    @Singleton
    @Provides
    fun provideLogger(): ExerciseLogger = AndroidLogExerciseLogger()

    @Singleton
    @Provides
    fun provideAwsIotService(logger: ExerciseLogger): AwsIotService {
        return AwsIotService(logger)
    }

    @Singleton
    @Provides
    fun provideAwsRetrofitApi(): AwsRetrofitApi {
        return AwsRetrofitApiProvider.awsRetrofitApi
    }

    @Singleton
    @Provides
    fun provideAwsApiGatewayService(awsRetrofitApi: AwsRetrofitApi): AwsApiGatewayService {
        return AwsApiGatewayServiceImpl(awsRetrofitApi)
    }

    @Singleton
    @Provides
    fun provideSharedService(): SharedService {
        return SharedService()
    }
}
