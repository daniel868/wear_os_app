package com.example.exercisesamplecompose.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class HeartRateStatus(
    @JsonProperty("average")
    val average: Double,
    @JsonProperty("min")
    val min: Double,
    @JsonProperty("max")
    val max: Double,
    val workoutId: String
)
