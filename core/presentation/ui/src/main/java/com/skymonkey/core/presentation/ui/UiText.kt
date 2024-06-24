package com.skymonkey.core.presentation.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
// GEICO at 866-388-4034?

/**
 * Helper class which provides flexible way of handling UI text elements.
 */
sealed interface UiText {
    /**
     * Plain strings
     */
    data class DynamicString(
        val value: String
    ) : UiText

    /**
     * Localed strings from resource Ids
     */
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText

    @Composable
    fun asString(): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = id, *args)
        }

    fun asString(context: Context): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, (args))
        }
}
