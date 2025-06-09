package com.example.exercisesamplecompose.utils

import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.data.AggregateDataType
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataType.Companion.ABSOLUTE_ELEVATION
import androidx.health.services.client.data.DataType.Companion.ABSOLUTE_ELEVATION_STATS
import androidx.health.services.client.data.DataType.Companion.ACTIVE_EXERCISE_DURATION_TOTAL
import androidx.health.services.client.data.DataType.Companion.CALORIES
import androidx.health.services.client.data.DataType.Companion.CALORIES_DAILY
import androidx.health.services.client.data.DataType.Companion.CALORIES_TOTAL
import androidx.health.services.client.data.DataType.Companion.DECLINE_DISTANCE
import androidx.health.services.client.data.DataType.Companion.DECLINE_DISTANCE_TOTAL
import androidx.health.services.client.data.DataType.Companion.DECLINE_DURATION
import androidx.health.services.client.data.DataType.Companion.DECLINE_DURATION_TOTAL
import androidx.health.services.client.data.DataType.Companion.DISTANCE
import androidx.health.services.client.data.DataType.Companion.DISTANCE_DAILY
import androidx.health.services.client.data.DataType.Companion.DISTANCE_TOTAL
import androidx.health.services.client.data.DataType.Companion.ELEVATION_GAIN
import androidx.health.services.client.data.DataType.Companion.ELEVATION_GAIN_TOTAL
import androidx.health.services.client.data.DataType.Companion.ELEVATION_LOSS
import androidx.health.services.client.data.DataType.Companion.ELEVATION_LOSS_TOTAL
import androidx.health.services.client.data.DataType.Companion.FLAT_GROUND_DISTANCE
import androidx.health.services.client.data.DataType.Companion.FLAT_GROUND_DISTANCE_TOTAL
import androidx.health.services.client.data.DataType.Companion.FLAT_GROUND_DURATION
import androidx.health.services.client.data.DataType.Companion.FLAT_GROUND_DURATION_TOTAL
import androidx.health.services.client.data.DataType.Companion.FLOORS
import androidx.health.services.client.data.DataType.Companion.FLOORS_DAILY
import androidx.health.services.client.data.DataType.Companion.FLOORS_TOTAL
import androidx.health.services.client.data.DataType.Companion.GOLF_SHOT_COUNT
import androidx.health.services.client.data.DataType.Companion.GOLF_SHOT_COUNT_TOTAL
import androidx.health.services.client.data.DataType.Companion.HEART_RATE_BPM
import androidx.health.services.client.data.DataType.Companion.HEART_RATE_BPM_STATS
import androidx.health.services.client.data.DataType.Companion.INCLINE_DISTANCE
import androidx.health.services.client.data.DataType.Companion.INCLINE_DISTANCE_TOTAL
import androidx.health.services.client.data.DataType.Companion.INCLINE_DURATION
import androidx.health.services.client.data.DataType.Companion.INCLINE_DURATION_TOTAL
import androidx.health.services.client.data.DataType.Companion.LOCATION
import androidx.health.services.client.data.DataType.Companion.PACE
import androidx.health.services.client.data.DataType.Companion.PACE_STATS
import androidx.health.services.client.data.DataType.Companion.REP_COUNT
import androidx.health.services.client.data.DataType.Companion.REP_COUNT_TOTAL
import androidx.health.services.client.data.DataType.Companion.RESTING_EXERCISE_DURATION
import androidx.health.services.client.data.DataType.Companion.RESTING_EXERCISE_DURATION_TOTAL
import androidx.health.services.client.data.DataType.Companion.RUNNING_STEPS
import androidx.health.services.client.data.DataType.Companion.RUNNING_STEPS_TOTAL
import androidx.health.services.client.data.DataType.Companion.SPEED
import androidx.health.services.client.data.DataType.Companion.SPEED_STATS
import androidx.health.services.client.data.DataType.Companion.STEPS
import androidx.health.services.client.data.DataType.Companion.STEPS_DAILY
import androidx.health.services.client.data.DataType.Companion.STEPS_PER_MINUTE
import androidx.health.services.client.data.DataType.Companion.STEPS_PER_MINUTE_STATS
import androidx.health.services.client.data.DataType.Companion.STEPS_TOTAL
import androidx.health.services.client.data.DataType.Companion.SWIMMING_LAP_COUNT
import androidx.health.services.client.data.DataType.Companion.SWIMMING_STROKES
import androidx.health.services.client.data.DataType.Companion.SWIMMING_STROKES_TOTAL
import androidx.health.services.client.data.DataType.Companion.VO2_MAX
import androidx.health.services.client.data.DataType.Companion.VO2_MAX_STATS
import androidx.health.services.client.data.DataType.Companion.WALKING_STEPS
import androidx.health.services.client.data.DataType.Companion.WALKING_STEPS_TOTAL
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.data.StatisticalDataPoint
import com.example.exercisesamplecompose.data.ExerciseClientManager

fun DataType.Companion.fromName(name: String): Set<DataType<*, *>> {
    return ALL_DATA_TYPES.filter { it.name == name }.toSet()
}

fun DataType.Companion.Wrapper(name: String, exerciseUpdate: ExerciseUpdate) {
    val dataTypesForName = ALL_DATA_TYPES.filter { it.name == name }.toSet()
    dataTypesForName.forEach {
        if (it is AggregateDataType<*, *>) {
            val updatedValue = exerciseUpdate.latestMetrics.getData(it)
            if (updatedValue is StatisticalDataPoint<*>) {
            }
        }
    }
}

private val ALL_DATA_TYPES = setOf(
    ABSOLUTE_ELEVATION_STATS,
    ACTIVE_EXERCISE_DURATION_TOTAL,
    CALORIES_TOTAL,
    DECLINE_DISTANCE_TOTAL,
    DECLINE_DURATION_TOTAL,
    DISTANCE_TOTAL,
    ELEVATION_GAIN_TOTAL,
    ELEVATION_LOSS_TOTAL,
    FLAT_GROUND_DISTANCE_TOTAL,
    FLAT_GROUND_DURATION_TOTAL,
    FLOORS_TOTAL,
    GOLF_SHOT_COUNT_TOTAL,
    HEART_RATE_BPM_STATS,
    INCLINE_DISTANCE_TOTAL,
    INCLINE_DURATION_TOTAL,
    PACE_STATS,
    REP_COUNT_TOTAL,
    RESTING_EXERCISE_DURATION_TOTAL,
    RUNNING_STEPS_TOTAL,
    SPEED_STATS,
    STEPS_PER_MINUTE_STATS,
    STEPS_TOTAL,
    SWIMMING_STROKES_TOTAL,
    VO2_MAX_STATS,
    WALKING_STEPS_TOTAL,
    ABSOLUTE_ELEVATION,
    CALORIES,
    CALORIES_DAILY,
    DISTANCE_DAILY,
    FLOORS_DAILY,
    STEPS_DAILY,
    DECLINE_DISTANCE,
    DECLINE_DURATION,
    DISTANCE,
    ELEVATION_GAIN,
    ELEVATION_LOSS,
    FLAT_GROUND_DISTANCE,
    FLAT_GROUND_DURATION,
    FLOORS,
    GOLF_SHOT_COUNT,
    HEART_RATE_BPM,
    INCLINE_DISTANCE,
    INCLINE_DURATION,
    LOCATION,
    PACE,
    REP_COUNT,
    RESTING_EXERCISE_DURATION,
    RUNNING_STEPS,
    SPEED,
    STEPS,
    STEPS_PER_MINUTE,
    SWIMMING_LAP_COUNT,
    SWIMMING_STROKES,
    VO2_MAX,
    WALKING_STEPS
)

/*
Distance class java.lang.Double
Calories class java.lang.Double
Absolute Elevation class java.lang.Double
Location class androidx.health.services.client.data.LocationData
HeartRate class java.lang.Double
Speed class java.lang.Double
Elevation Gain class java.lang.Double
Pace class java.lang.Double
Elevation Loss class java.lang.Double
Steps class java.lang.Long
Step per minute class java.lang.Long
Floors class java.lang.Double
Golf Shot Count class java.lang.Long
Ground Contact Time long
Ground Contact Balance double
Vertical Oscillation double
Vertical Ratio double
Stride Length double
 */