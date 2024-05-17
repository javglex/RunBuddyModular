package com.skymonkey.core.presentation.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

/**
 * Helper class which provides flexible way of handling UI text elements.
 */
sealed interface UiText {
    /**
     * Plain strings
     */
    data class DynamicString(val value: String): UiText

    /**
     * Localed strings from resource Ids
     */
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf(),
        ): UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = id, *args)
        }
    }

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, (args))
        }
    }
}