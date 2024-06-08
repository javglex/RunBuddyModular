package com.skymonkey.wear.run.presentation

sealed interface TrackerEvent {
    data object RunFinished: TrackerEvent
}