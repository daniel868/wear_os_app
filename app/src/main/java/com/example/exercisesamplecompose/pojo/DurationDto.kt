package com.example.exercisesamplecompose.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DurationDto(
    @JsonProperty("hours")
    val hours: Long,
    @JsonProperty("minutes")
    val minute: Long,
    @JsonProperty("seconds")
    val seconds: Long
)
