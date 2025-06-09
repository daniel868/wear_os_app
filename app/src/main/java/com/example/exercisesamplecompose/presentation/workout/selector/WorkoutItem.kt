package com.example.exercisesamplecompose.presentation.workout.selector

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Sports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices

@Composable
fun WorkoutItem(
    workoutType: String,
    onItemClick: () -> Unit
) {
    val workoutName = workoutType.replace("_", " ").lowercase()
        .split(" ").joinToString(" ") { it.capitalize(Locale.current) }
    Chip(
        label = {
            Column {
                Text(workoutName, fontSize = 10.sp, color = Color.White)
            }
        },
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .background(color = Color(0xFF4CAF50), shape = CircleShape)
            ) {
                Icon(
                    painter = rememberVectorPainter(getWorkoutIcon(workoutType)),
                    contentDescription = workoutType,
                    tint = Color.Black,
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                )
            }

        },
        modifier = Modifier
            .padding(top = 5.dp, start = 10.dp, end = 10.dp)
            .fillMaxSize(),
        colors = ChipDefaults.chipColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            secondaryContentColor = Color.White,
            disabledContentColor = Color.Gray,
        ),
        border = ChipDefaults.chipBorder(
            borderStroke = BorderStroke(1.dp, Color.Gray)
        ),
        onClick = onItemClick
    )

}

@WearPreviewDevices
@Composable
fun WorkoutItemPreview() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            WorkoutItem("BENCH_PRESS", {})
        }
        item {
            WorkoutItem("RUNNING", {})
        }
    }

}

fun getWorkoutIcon(workoutType: String): ImageVector {
    return when (workoutType) {
        "RUNNING", "RUNNING_TREADMILL" -> Icons.AutoMirrored.Filled.DirectionsRun
        "ALPINE_SKIING" -> Icons.Default.DownhillSkiing
        "BACKPACKING" -> Icons.Default.Hiking
        "BADMINTON", "TENNIS", "TABLE_TENNIS", "SQUASH" -> Icons.Default.SportsTennis
        "BASEBALL", "SOFTBALL" -> Icons.Default.SportsBaseball
        "BASKETBALL" -> Icons.Default.SportsBasketball
        "BENCH_PRESS", "BARBELL_SHOULDER_PRESS", "WEIGHTLIFTING" -> Icons.Default.FitnessCenter
        "BIKING", "MOUNTAIN_BIKING" -> Icons.Default.DirectionsBike
        "BIKING_STATIONARY" -> Icons.Default.PedalBike
        "BOXING", "MARTIAL_ARTS" -> Icons.Default.SportsMma
        "CRICKET" -> Icons.Default.SportsCricket
        "CROSS_COUNTRY_SKIING" -> Icons.Default.NordicWalking
        "DANCING" -> Icons.Default.MusicNote
        "DEADLIFT", "SQUAT" -> Icons.Default.FitnessCenter
        "ELLIPTICAL" -> Icons.Default.ElectricBike
        "FOOTBALL_AMERICAN", "FOOTBALL_AUSTRALIAN", "RUGBY" -> Icons.Default.SportsFootball
        "GOLF" -> Icons.Default.SportsGolf
        "GYMNASTICS" -> Icons.Default.AcUnit
        "HANDBALL" -> Icons.Default.SportsHandball
        "HIGH_INTENSITY_INTERVAL_TRAINING" -> Icons.Default.VolunteerActivism
        "HIKING" -> Icons.Default.Hiking
        "HORSE_RIDING" -> Icons.Default.VolunteerActivism
        "ICE_HOCKEY", "ROLLER_HOCKEY" -> Icons.Default.SportsHockey
        "ICE_SKATING", "SKATING" -> Icons.Default.IceSkating
        "LAT_PULL_DOWN", "LUNGE" -> Icons.Default.FitnessCenter
        "PILATES", "YOGA" -> Icons.Default.SelfImprovement
        "PLANK", "CRUNCH", "BACK_EXTENSION" -> Icons.Default.AccessibilityNew
        "ROWING", "ROWING_MACHINE" -> Icons.Default.Rowing
        "SAILING", "YACHTING" -> Icons.Default.Sailing
        "SCUBA_DIVING" -> Icons.Default.ScubaDiving
        "SKIING" -> Icons.Default.DownhillSkiing
        "SNOWBOARDING" -> Icons.Default.Snowboarding
        "SNOWSHOEING" -> Icons.Default.Snowshoeing
        "SOCCER" -> Icons.Default.SportsSoccer
        "STAIR_CLIMBING", "STAIR_CLIMBING_MACHINE" -> Icons.Default.Stairs
        "SWIMMING_OPEN_WATER", "SWIMMING_POOL", "WATER_POLO" -> Icons.Default.Pool
        "VOLLEYBALL" -> Icons.Default.SportsVolleyball
        "WALKING" -> Icons.Default.DirectionsWalk
        "MEDITATION" -> Icons.Default.Spa
        else -> Icons.Default.Sports
    }
}

