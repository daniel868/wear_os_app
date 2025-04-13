/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(ExperimentalHorologistApi::class)

package com.example.exercisesamplecompose.presentation.exercise

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Text
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import com.example.exercisesamplecompose.R
import com.example.exercisesamplecompose.data.ServiceState
import com.example.exercisesamplecompose.presentation.component.CaloriesText
import com.example.exercisesamplecompose.presentation.component.DistanceText
import com.example.exercisesamplecompose.presentation.component.HRText
import com.example.exercisesamplecompose.presentation.component.LocationText
import com.example.exercisesamplecompose.presentation.component.PauseButton
import com.example.exercisesamplecompose.presentation.component.ResumeButton
import com.example.exercisesamplecompose.presentation.component.StartButton
import com.example.exercisesamplecompose.presentation.component.StepPerMinuteText
import com.example.exercisesamplecompose.presentation.component.StopButton
import com.example.exercisesamplecompose.presentation.component.formatElapsedTime
import com.example.exercisesamplecompose.presentation.dialogs.ExerciseGoalMet
import com.example.exercisesamplecompose.presentation.summary.SummaryScreenState
import com.example.exercisesamplecompose.presentation.theme.ThemePreview
import com.example.exercisesamplecompose.service.ExerciseServiceState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.ambient.AmbientState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.material.AlertDialog
import com.google.android.horologist.health.composables.ActiveDurationText

@Composable
fun ExerciseRoute(
    ambientState: AmbientState,
    modifier: Modifier = Modifier,
    onSummary: (SummaryScreenState) -> Unit,
    onRestart: () -> Unit,
    onFinishActivity: () -> Unit,
) {
    val viewModel = hiltViewModel<ExerciseViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isEnded) {
        SideEffect {
            onSummary(uiState.toSummary())
        }
    }

    if (uiState.error != null) {
        ErrorStartingExerciseScreen(
            onRestart = onRestart, onFinishActivity = onFinishActivity, uiState = uiState
        )
    } else if (ambientState is AmbientState.Interactive) {
        ExerciseScreen(
            onPauseClick = { viewModel.pauseExercise() },
            onEndClick = { viewModel.endExercise() },
            onResumeClick = { viewModel.resumeExercise() },
            onStartClick = { viewModel.startExercise() },
            uiState = uiState,
            modifier = modifier
        )
    }
}

/**
 * Shows an error that occurred when starting an exercise
 */
@Composable
fun ErrorStartingExerciseScreen(
    onRestart: () -> Unit, onFinishActivity: () -> Unit, uiState: ExerciseScreenState
) {
    AlertDialog(
        title = stringResource(id = R.string.error_starting_exercise),
        message = "${uiState.error ?: stringResource(id = R.string.unknown_error)}. ${
            stringResource(
                id = R.string.try_again
            )
        }",
        onCancel = onFinishActivity,
        onOk = onRestart,
        showDialog = true,
    )
}

/**
 * Shows while an exercise is in progress
 */
@Composable
fun ExerciseScreen(
    onPauseClick: () -> Unit,
    onEndClick: () -> Unit,
    onResumeClick: () -> Unit,
    onStartClick: () -> Unit,
    uiState: ExerciseScreenState,
    modifier: Modifier = Modifier
) {

    //Page Indicator Values
    val maxPages = 3
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { maxPages })

    val pageIndicatorState = remember {
        object : PageIndicatorState {
            override val selectedPage: Int
                get() = pagerState.currentPage
            override val pageCount: Int
                get() = maxPages
            override val pageOffset: Float
                get() = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        HorizontalPageIndicator(
            pageIndicatorState = pageIndicatorState,
            selectedColor = MaterialTheme.colors.secondary,
            unselectedColor = MaterialTheme.colors.onSecondary,
        )
        ScreenScaffold {
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> {
                        ExerciseControlButtons(
                            uiState,
                            onStartClick,
                            onEndClick,
                            onResumeClick,
                            onPauseClick,
                            pagerState
                        )
                    }

                    1 -> {
                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HeartRateRow(uiState)

                            CaloriesRow(uiState)

                            DistanceAndLapsRow(uiState)

                            DurationRow(uiState)


                        }
                    }

                    2 -> {
                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StepsPerMinute(uiState)

                            LocationCompose(uiState)
                        }
                    }
                }
            }
        }

    }

    //If we meet an exercise goal, show our exercise met dialog.
    //This approach is for the sample, and doesn't guarantee processing of this event in all cases,
    //such as the user exiting the app while this is in-progress. Consider alternatives to exposing
    //state in a production app.
    uiState.exerciseState?.exerciseGoal?.let {
        Log.d("ExerciseGoalMet", "Showing exercise goal met dialog")
        ExerciseGoalMet(it.isNotEmpty())
    }
}

@Composable
private fun ExerciseControlButtons(
    uiState: ExerciseScreenState,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onResumeClick: () -> Unit,
    onPauseClick: () -> Unit,
    pagerState: PagerState
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.isEnding) {
                StartButton(onStartClick)
            } else {
                StopButton(onEndClick)
            }
            if (uiState.isPaused) {
                ResumeButton(onResumeClick)
                //When the user clicks resume, scroll to the main screen
                LaunchedEffect(pagerState) {
                    pagerState.animateScrollToPage(1)
                }

            } else {
                PauseButton(onPauseClick)
                //When the user clicks pause, scroll to the main screen
                LaunchedEffect(pagerState) {
                    pagerState.animateScrollToPage(1)
                }

            }
        }
    }
}

@Composable
private fun DistanceAndLapsRow(uiState: ExerciseScreenState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Row {
            DistanceText(uiState.exerciseState?.exerciseMetrics?.distance)
        }
    }
}

@Composable
private fun HeartRateRow(uiState: ExerciseScreenState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Row {
            HRText(
                hr = uiState.exerciseState?.exerciseMetrics?.heartRate
            )
        }
    }
}

@Composable
private fun CaloriesRow(uiState: ExerciseScreenState) {
    Row {
        CaloriesText(
            uiState.exerciseState?.exerciseMetrics?.calories
        )
    }
}

@Composable
private fun StepsPerMinute(uiState: ExerciseScreenState) {
    Row {
        StepPerMinuteText(
            stepsPerMinute = uiState.exerciseState?.exerciseMetrics?.stepsPerMinute.toString()
        )
    }
}

@Composable
private fun LocationCompose(uitState: ExerciseScreenState) {
    Row {
        LocationText(
            location = uitState.exerciseState?.exerciseMetrics?.location
        )
    }
}


@Composable
private fun DurationRow(uiState: ExerciseScreenState) {
    val lastActiveDurationCheckpoint = uiState.exerciseState?.activeDurationCheckpoint
    val exerciseState = uiState.exerciseState?.exerciseState
    Row(
        horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            if (exerciseState != null && lastActiveDurationCheckpoint != null) {
                ActiveDurationText(
                    checkpoint = lastActiveDurationCheckpoint,
                    state = uiState.exerciseState.exerciseState
                ) {
                    Text(
                        text = formatElapsedTime(it, includeSeconds = true),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondary,
                        fontSize = 25.sp
                    )
                }
            } else {
                Text(text = "--")
            }
        }
    }
}

@WearPreviewDevices
@Composable
fun ExerciseScreenPreview() {
    ThemePreview {
        ExerciseScreen(
            onPauseClick = {},
            onEndClick = {},
            onResumeClick = {},
            onStartClick = {},
            uiState = ExerciseScreenState(
                hasExerciseCapabilities = true,
                isTrackingAnotherExercise = false,
                serviceState = ServiceState.Connected(
                    ExerciseServiceState()
                ),
                exerciseState = ExerciseServiceState()
            ),
        )
    }
}

@WearPreviewDevices
@Composable
fun ErrorStartingExerciseScreenPreview() {
    ThemePreview {
        ErrorStartingExerciseScreen(
            onRestart = {},
            onFinishActivity = {},
            uiState = ExerciseScreenState(
                hasExerciseCapabilities = true,
                isTrackingAnotherExercise = false,
                serviceState = ServiceState.Connected(
                    ExerciseServiceState()
                ),
                exerciseState = ExerciseServiceState()
            )
        )
    }
}

@WearPreviewDevices
@Composable
fun ExerciseControlButtonsPreview() {
    ThemePreview {
        ExerciseControlButtons(uiState = ExerciseScreenState(
            hasExerciseCapabilities = true,
            isTrackingAnotherExercise = false,
            serviceState = ServiceState.Connected(
                ExerciseServiceState()
            ),
            exerciseState = ExerciseServiceState()
        ), onStartClick = {},
            onEndClick = {},
            onResumeClick = {},
            onPauseClick = {},
            pagerState = PagerState { 0 })
    }
}
