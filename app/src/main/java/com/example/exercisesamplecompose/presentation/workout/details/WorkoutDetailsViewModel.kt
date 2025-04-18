package com.example.exercisesamplecompose.presentation.workout.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercisesamplecompose.data.ExerciseClientManager
import com.example.exercisesamplecompose.pojo.DurationDto
import com.example.exercisesamplecompose.pojo.WorkoutDetailsDto
import com.example.exercisesamplecompose.service.ExerciseLogger
import com.example.exercisesamplecompose.service.aws.AwsApiGatewayService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val awsApiGatewayService: AwsApiGatewayService,
    private val logger: ExerciseLogger,
    private val exerciseClientManager: ExerciseClientManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<WorkoutDetailsScreenState>(WorkoutDetailsScreenState.Loading)

    val uiState: StateFlow<WorkoutDetailsScreenState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = WorkoutDetailsScreenState.Loading
            try {
                val workoutId: String = exerciseClientManager.workoutId!!
                logger.log("Fetching workout information for workoutId: $workoutId")
                val workoutDetails =
                    awsApiGatewayService.getWorkoutDetails(workoutId)
                _uiState.value = WorkoutDetailsScreenState.Success(workoutDetails)
            } catch (e: Exception) {
                _uiState.value = WorkoutDetailsScreenState.Error("Error getting workout details: " + e.message)
                logger.error("Error getting workout details: " + e.message, e)
            }
        }
    }
}