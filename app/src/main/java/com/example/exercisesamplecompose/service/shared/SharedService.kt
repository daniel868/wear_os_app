package com.example.exercisesamplecompose.service.shared

import com.example.exercisesamplecompose.presentation.shared.SharedDataState

class SharedService {
    private var _sharedDataState =
        SharedDataState(selectedWorkoutExercise = null, isGpsRequired = false)

    fun updateSharedDataState(sharedDataState: SharedDataState) {
        this._sharedDataState = sharedDataState;
    }

    fun clearOldValues() {
        this._sharedDataState = SharedDataState(selectedWorkoutExercise = null, isGpsRequired = false)
    }


    val sharedDataState: SharedDataState
        get() = _sharedDataState
}