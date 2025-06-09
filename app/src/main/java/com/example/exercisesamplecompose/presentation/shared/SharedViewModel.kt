package com.example.exercisesamplecompose.presentation.shared

import androidx.lifecycle.ViewModel
import com.example.exercisesamplecompose.pojo.WorkoutInfo
import com.example.exercisesamplecompose.service.shared.SharedService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val sharedService: SharedService
) : ViewModel() {
    val _uiState =
        MutableStateFlow(
            SharedDataState(selectedWorkoutExercise = null, isGpsRequired = false)
        )

    val uiState: StateFlow<SharedDataState> = _uiState.asStateFlow()

    fun updateWorkoutType(workoutInfo: WorkoutInfo) {
        this._uiState.update { old ->
            old.copy(
                selectedWorkoutExercise = workoutInfo.workoutType,
                isGpsRequired = workoutInfo.isGpsRequired,
                trackingDataTypes = workoutInfo.supportedFeature
            )
        }
        this.sharedService.updateSharedDataState(this._uiState.value)
    }
}