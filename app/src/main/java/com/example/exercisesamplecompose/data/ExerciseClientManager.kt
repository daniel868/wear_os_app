package com.example.exercisesamplecompose.data

import android.annotation.SuppressLint
import android.os.FileUtils
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.ComparisonType
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeCondition
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseGoal
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseTypeCapabilities
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationAvailability
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCapabilities
import androidx.health.services.client.markLap
import androidx.health.services.client.pauseExercise
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.resumeExercise
import androidx.health.services.client.startExercise
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.paging.LOGGER
import com.example.exercisesamplecompose.pojo.WorkoutStatus
import com.example.exercisesamplecompose.service.ExerciseLogger
import com.example.exercisesamplecompose.service.aws.AwsIotService
import com.example.exercisesamplecompose.utils.fromName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.ArrayList
import java.util.Date
import java.util.UUID
import java.util.logging.Level
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration


@SuppressLint("RestrictedApi")
@Singleton
class ExerciseClientManager @Inject constructor(
    healthServicesClient: HealthServicesClient,
    private val logger: ExerciseLogger,
    serviceClient: AwsIotService
) {
    val exerciseClient: ExerciseClient = healthServicesClient.exerciseClient
    val dynamoDbService = serviceClient;
    var workoutId: String? = null;
    var startWorkoutTime: Date? = null

    suspend fun getExerciseCapabilities(): ExerciseTypeCapabilities? {
        val capabilities = exerciseClient.getCapabilities()

        return if (ExerciseType.RUNNING in capabilities.supportedExerciseTypes) {
            capabilities.getExerciseTypeCapabilities(ExerciseType.RUNNING)
        } else {
            null
        }
    }

    private var thresholds = Thresholds(0.0, Duration.ZERO)

    fun updateGoals(newThresholds: Thresholds) {
        thresholds = newThresholds.copy()
    }

    suspend fun startExercise() {
        logger.log("Starting exercise")
        // Types for which we want to receive metrics. Only ask for ones that are supported.
        val capabilities = getExerciseCapabilities()

        if (capabilities == null) {
            logger.log("No capabilities")
            return
        }

        val testDataTypes = DataType.fromName("HeartRate")

        val dataTypes = setOf(
            DataType.HEART_RATE_BPM,
            DataType.HEART_RATE_BPM_STATS,
            DataType.CALORIES_TOTAL,
            DataType.DISTANCE_TOTAL,
            DataType.STEPS_PER_MINUTE,
            DataType.LOCATION
        ).intersect(capabilities.supportedDataTypes)
        val exerciseGoals = mutableListOf<ExerciseGoal<*>>()
        if (supportsCalorieGoal(capabilities)) {
            // Create a one-time goal.
            exerciseGoals.add(
                ExerciseGoal.createOneTimeGoal(
                    DataTypeCondition(
                        dataType = DataType.CALORIES_TOTAL,
                        threshold = CALORIES_THRESHOLD,
                        comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL
                    )
                )
            )
        }

        // Set a distance goal if it's supported by the exercise and the user has entered one
        if (supportsDistanceMilestone(capabilities) && thresholds.distanceIsSet) {
            exerciseGoals.add(
                ExerciseGoal.createOneTimeGoal(
                    condition = DataTypeCondition(
                        dataType = DataType.DISTANCE_TOTAL,
                        threshold = thresholds.distance * 1000, //our app uses kilometers
                        comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL
                    )
                )
            )
        }

        // Set a duration goal if it's supported by the exercise and the user has entered one
        if (supportsDurationMilestone(capabilities) && thresholds.durationIsSet) {
            exerciseGoals.add(
                ExerciseGoal.createOneTimeGoal(
                    DataTypeCondition(
                        dataType = DataType.ACTIVE_EXERCISE_DURATION_TOTAL,
                        threshold = thresholds.duration.inWholeSeconds,
                        comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL
                    )
                )
            )
        }


        val supportsAutoPauseAndResume = capabilities.supportsAutoPauseAndResume

        val config = ExerciseConfig(
            exerciseType = ExerciseType.RUNNING,
            dataTypes = dataTypes,
            isAutoPauseAndResumeEnabled = supportsAutoPauseAndResume,
            isGpsEnabled = true,
            exerciseGoals = exerciseGoals
        )

        exerciseClient.startExercise(config)

        try {
            this.workoutId = UUID.randomUUID().toString()
            this.startWorkoutTime = Date()
            val workoutStatus =
                WorkoutStatus(
                    this.workoutId!!,
                    ExerciseType.RUNNING.name,
                    this.startWorkoutTime,
                    null
                )
            dynamoDbService.publishDataToIotCore(workoutStatus, "data/workout");
        } catch (e: Exception) {
            logger.error("Error inserting into dynamo", e)
        }


        logger.log("Started exercise")
    }

    suspend fun prepareExercise() {
        logger.log("Preparing an exercise")
        val warmUpConfig = WarmUpConfig(
            exerciseType = ExerciseType.RUNNING,
            dataTypes = setOf(DataType.HEART_RATE_BPM, DataType.LOCATION)
        )
        try {
            exerciseClient.prepareExercise(warmUpConfig)
        } catch (e: Exception) {
            logger.log("Prepare exercise failed - ${e.message}")
        }
    }

    suspend fun endExercise() {
        logger.log("Ending exercise")
        exerciseClient.endExercise()
        try {
            val workoutStatus =
                WorkoutStatus(
                    this.workoutId!!,
                    ExerciseType.RUNNING.name,
                    this.startWorkoutTime,
                    Date()
                )
            dynamoDbService.publishDataToIotCore(workoutStatus, "data/workout");
            this.startWorkoutTime = null
        } catch (e: Exception) {
            logger.error("Error inserting into dynamo", e)
        }
    }

    suspend fun pauseExercise() {
        logger.log("Pausing exercise")
        exerciseClient.pauseExercise()
    }

    suspend fun resumeExercise() {
        logger.log("Resuming exercise")
        exerciseClient.resumeExercise()
    }


    suspend fun markLap() {
        if (exerciseClient.isExerciseInProgress()) {
            exerciseClient.markLap()
        }
    }

    val exerciseUpdateFlow = callbackFlow {
        val callback = object : ExerciseUpdateCallback {
            override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                trySendBlocking(ExerciseMessage.ExerciseUpdateMessage(update))
            }

            override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
                trySendBlocking(ExerciseMessage.LapSummaryMessage(lapSummary))
            }

            override fun onRegistered() {
            }

            override fun onRegistrationFailed(throwable: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onAvailabilityChanged(
                dataType: DataType<*, *>, availability: Availability
            ) {
                if (availability is LocationAvailability) {
                    trySendBlocking(ExerciseMessage.LocationAvailabilityMessage(availability))
                }
            }
        }

        exerciseClient.setUpdateCallback(callback)
        awaitClose {
            exerciseClient.clearUpdateCallbackAsync(callback)
        }
    }

    private companion object {
        const val CALORIES_THRESHOLD = 250.0
    }
}

data class Thresholds(
    var distance: Double,
    var duration: Duration,
    var durationIsSet: Boolean = duration != Duration.ZERO,
    var distanceIsSet: Boolean = distance != 0.0,
)


sealed class ExerciseMessage {
    class ExerciseUpdateMessage(val exerciseUpdate: ExerciseUpdate) : ExerciseMessage()
    class LapSummaryMessage(val lapSummary: ExerciseLapSummary) : ExerciseMessage()
    class LocationAvailabilityMessage(val locationAvailability: LocationAvailability) :
        ExerciseMessage()
}
