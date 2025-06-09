package com.example.exercisesamplecompose.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkoutDetailsDto(
    @JsonProperty("workoutType")
    val workoutType: String,
    @JsonProperty("startTime")
    val startTime: Long,
    @JsonProperty("finishTime")
    val finishTime: Long,
    @JsonProperty("durationDto")
    val durationDto: DurationDto,
    @JsonProperty("heartRateStatus")
    val heartRateStatus: List<HeartRateDto>,
    @JsonProperty("locations")
    val locations: List<LocationDataDto>
)