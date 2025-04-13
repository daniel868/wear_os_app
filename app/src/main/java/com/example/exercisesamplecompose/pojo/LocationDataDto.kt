package com.example.exercisesamplecompose.pojo

import androidx.health.services.client.data.LocationData
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.math.RoundingMode

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationDataDto(
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("longitude")
    val longitude: Double
)


object LocationDataDtoMapper {
    fun mapFromLocationDataToDto(locationData: LocationData): LocationDataDto {
        locationData.longitude
        locationData.altitude
        return LocationDataDto(
            latitude = BigDecimal(locationData.latitude).setScale(4, RoundingMode.HALF_UP)
                .toDouble(),
            longitude = BigDecimal(locationData.longitude).setScale(4, RoundingMode.HALF_UP)
                .toDouble()
        )
    }
}

