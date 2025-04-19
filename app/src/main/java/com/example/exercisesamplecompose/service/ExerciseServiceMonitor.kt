package com.example.exercisesamplecompose.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.util.Log
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.LocationData
import androidx.health.services.client.data.SampleDataPoint
import androidx.health.services.client.data.StatisticalDataPoint
import com.example.exercisesamplecompose.data.ExerciseClientManager
import com.example.exercisesamplecompose.data.ExerciseMessage
import com.example.exercisesamplecompose.pojo.HeartRateStatus
import com.example.exercisesamplecompose.pojo.LocationDataDtoMapper
import com.example.exercisesamplecompose.pojo.LocationDataStatus
import com.example.exercisesamplecompose.service.aws.AwsIotService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.Date
import javax.inject.Inject

class ExerciseServiceMonitor @Inject constructor(
    val exerciseClientManager: ExerciseClientManager,
    val service: Service,
    val awsIotService: AwsIotService,
    @ApplicationContext val appContext: Context
) {
    // TODO behind an interface
    val exerciseService = service as ExerciseService


    private var lastUpdateDate: Date? = null;

    val exerciseServiceState = MutableStateFlow(
        ExerciseServiceState(
            exerciseState = null,
            exerciseMetrics = ExerciseMetrics()
        )
    )

    suspend fun monitor() {
        exerciseClientManager.exerciseUpdateFlow.collect {
            when (it) {
                is ExerciseMessage.ExerciseUpdateMessage ->
                    processExerciseUpdate(it.exerciseUpdate)

                is ExerciseMessage.LapSummaryMessage ->
                    exerciseServiceState.update { oldState ->
                        oldState.copy(
                            exerciseLaps = it.lapSummary.lapCount
                        )
                    }

                is ExerciseMessage.LocationAvailabilityMessage ->
                    exerciseServiceState.update { oldState ->
                        oldState.copy(
                            locationAvailability = it.locationAvailability
                        )
                    }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun processExerciseUpdate(exerciseUpdate: ExerciseUpdate) {

        if (exerciseUpdate.exerciseStateInfo.state.isEnded) {
            exerciseService.removeOngoingActivityNotification()
        }

        val currentDate = Date()

        if (lastUpdateDate == null) {
            lastUpdateDate = currentDate;
        }

        val durationBetween: Duration =
            Duration.between(lastUpdateDate!!.toInstant(), currentDate.toInstant());

        if (durationBetween.toMinutes() >= 1) {

            val locationData: List<SampleDataPoint<LocationData>> =
                exerciseUpdate.latestMetrics.getData(DataType.LOCATION)
            locationData.forEach { it ->
                Log.d("com.example.exercisesamplecompose.service", "LocationValue : " + it)
                CoroutineScope(Dispatchers.IO).launch {
                    val locationDataStatus = LocationDataStatus(
                        LocationDataDtoMapper.mapFromLocationDataToDto(it.value),
                        exerciseClientManager.workoutId!!
                    )
                    awsIotService.publishDataToIotCore(locationDataStatus, "data/location")
                }
            }

            //TODO: modify here to sent the data point information
            val heartRateBpm: SampleDataPoint<Double>? =
                exerciseUpdate.latestMetrics.getData(DataType.HEART_RATE_BPM)[0];
//
            if (heartRateBpm != null) {
                val heartRateStatus = HeartRateStatus(
                    heartRateBpm.value,
                    heartRateBpm.value,
                    heartRateBpm.value,
                    exerciseClientManager.workoutId!!
                )
                CoroutineScope(Dispatchers.IO).launch {
                    awsIotService.publishDataToIotCore(heartRateStatus, "data/heartRateStatus")
                }
            }
            lastUpdateDate = currentDate;
        }

        exerciseServiceState.update { old ->
            old.copy(
                exerciseState = exerciseUpdate.exerciseStateInfo.state,
                exerciseMetrics = old.exerciseMetrics.update(exerciseUpdate.latestMetrics),
                activeDurationCheckpoint = exerciseUpdate.activeDurationCheckpoint
                    ?: old.activeDurationCheckpoint,
                exerciseGoal = exerciseUpdate.latestAchievedGoals
            )
        }
    }
}