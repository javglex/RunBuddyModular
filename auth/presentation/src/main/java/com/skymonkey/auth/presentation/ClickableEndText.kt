package com.skymonkey.auth.presentation

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.skymonkey.core.presentation.designsystem.Poppins
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme

/**
 * Makes a portion of a string of text clickable.
 * @param normalText - the un-clickable portion of the text
 * @param clickableText - the clickable portion of the text
 * @param onClick - invoked when clickable portion of text is clicked on.
 */
@Composable
fun ClickableEndText(
    normalText: String,
    clickableText: String,
    modifier: Modifier = Modifier,
    textStyle: SpanStyle =
        SpanStyle(
            fontFamily = Poppins,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    clickableTextStyle: SpanStyle =
        SpanStyle(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = Poppins
        ),
    onClick: () -> Unit,
) {
    val annotatedString =
        buildAnnotatedString {
            withStyle(
                style = textStyle
            ) {
                append("$normalText ")
                pushStringAnnotation(
                    tag = "clickable_text",
                    annotation = clickableText
                )
                withStyle(
                    style = clickableTextStyle
                ) {
                    append(clickableText)
                }
            }
        }
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString
                .getStringAnnotations(
                    tag = "clickable_text",
                    start = offset,
                    end = offset
                ).firstOrNull()
                ?.let {
                    onClick()
                }
        }
    )
}

@Preview
@Composable
private fun ClickableEndTextPreview() {
    RunBuddyTheme {
        ClickableEndText("This is regular text", "this is clickable") {}
    }
}
