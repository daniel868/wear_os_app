package com.example.exercisesamplecompose.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.provider.CallLog.Locations
import android.util.Log
import androidx.health.services.client.data.AggregateDataType
import androidx.health.services.client.data.DataPoint
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
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
import com.example.exercisesamplecompose.service.shared.SharedService
import com.example.exercisesamplecompose.utils.fromName
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

            var locationData: List<SampleDataPoint<LocationData>> =
                exerciseUpdate.latestMetrics.getData(DataType.LOCATION)
            if (locationData.isEmpty()) {
                Log.d("com.example.exercisesamplecompose.service", "Retry to read the location value : ")
                locationData = exerciseUpdate.latestMetrics.getData(DataType.LOCATION)
                Log.d("com.example.exercisesamplecompose.service", "Location value after re-reading: $locationData")
            }
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
            var heartRateBpm: SampleDataPoint<Double>? =
                exerciseUpdate.latestMetrics.getData(DataType.HEART_RATE_BPM).lastOrNull()

            Log.d("heartRate measure:", "$heartRateBpm")

//
//            val paceDataInfos: Set<DataType<*, *>> = DataType.fromName("Pace")
//            paceDataInfos.forEach { it ->
//                //AggregateDataType - value with average, min, max
//                if (it is AggregateDataType<*, *>) {
//                    val paceResponse: DataPoint<out Number>? =
//                        exerciseUpdate.latestMetrics.getData(it)
//
//                    Class.forName(it.valueClass.name)
//                    val statisticalDataPoint = paceResponse as StatisticalDataPoint<*>
//                    val stats: String =
//                        "min: ${statisticalDataPoint.min} average: ${statisticalDataPoint.average} max: ${statisticalDataPoint.average}"
//                    Log.d("paceResponseAggregate", stats)
//                }
//                //DeltaDataType - value at a certain point in time
//                //return a list with a single item
//                if (it is DeltaDataType<*, *>) {
//                    val paceResponse: List<DataPoint<out Any>> =
//                        exerciseUpdate.latestMetrics.getData(it)
//                    val trackedValue: Double =
//                        (paceResponse as List<SampleDataPoint<Double>>).lastOrNull()?.value ?: 0.0
//                    Log.d(
//                        "paceResponseDelta",
//                        trackedValue.toString()
//                    )
//                }
//            }

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