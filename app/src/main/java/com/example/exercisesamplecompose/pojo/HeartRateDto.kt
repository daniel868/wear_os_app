package com.example.exercisesamplecompose.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class HeartRateDto(
    @JsonProperty("average")
    val average: Double,
    @JsonProperty("min")
    val min: Double,
    @JsonProperty("max")
    val max: Double,
)
