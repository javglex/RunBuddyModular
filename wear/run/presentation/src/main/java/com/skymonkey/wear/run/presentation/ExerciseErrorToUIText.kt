package com.skymonkey.wear.run.presentation

import com.skymonkey.core.presentation.ui.UiText
import com.skymonkey.wear.run.domain.ExerciseError

fun ExerciseError.toUiText(): UiText? =
    when (this) {
        ExerciseError.ONGOING_EXERCISE -> UiText.StringResource(R.string.error_ongoing_exercise)
        ExerciseError.ONGOING_OTHER_EXERCISE -> UiText.StringResource(R.string.error_other_ongoing_exercise)
        ExerciseError.EXERCISE_ALREADY_ENDED -> UiText.StringResource(R.string.error_exercise_already_ended)
        ExerciseError.UNKNOWN -> UiText.StringResource(com.skymonkey.core.presentation.ui.R.string.error_unkown)
        ExerciseError.TRACKING_NOT_SUPPORTED -> null
    }
