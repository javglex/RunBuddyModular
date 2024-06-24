package com.skymonkey.core.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

fun ComponentActivity.shouldShowLocationPermissionRationale(): Boolean =
    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

fun ComponentActivity.shouldShowNotificationPermissionRationale(): Boolean =
    Build.VERSION.SDK_INT >= 33 &&
        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

fun Context.hasLocationPermission(): Boolean {
    return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) // if fine location granted, we can assume we have coarse permission
}

fun Context.hasBodyPermission(): Boolean = hasPermission(Manifest.permission.BODY_SENSORS)

fun Context.hasActivityPermission(): Boolean = hasPermission(Manifest.permission.ACTIVITY_RECOGNITION)

fun Context.hasNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= 33) {
        hasPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        true
    }

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
