package com.skymonkey.run.presentation.run_overview.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.run.presentation.R

@Composable
fun BuddyMainCard(
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    if(isExpanded) {
        Box(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.standard_pet_gw),
                contentDescription = stringResource(id = R.string.this_is_your_buddy),
                modifier = Modifier.clip(RoundedCornerShape(15.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-32).dp) // Adjust the offset as needed
            ) {
                SpeechBubble(
                    listOf(
                        stringResource(id = R.string.lets_go),
                        stringResource(id = R.string.dog_bark)
                    )
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.standard_pet_gw),
                contentDescription = stringResource(id = R.string.this_is_your_buddy),
                modifier = Modifier.clip(RoundedCornerShape(15.dp)),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-90).dp, y = (-32).dp) // Adjust the offset as needed
            ) {
                SpeechBubble(
                    listOf(
                        stringResource(id = R.string.lets_go),
                        stringResource(id = R.string.dog_bark)
                    ),
                    showBubbleTriangle = false
                )
            }
        }

    }
}

@Preview
@Composable
private fun BuddyMainCardPreview() {
    RunBuddyTheme {
        BuddyMainCard(isExpanded = true)
    }
}

@Preview
@Composable
private fun BuddyMainCardMinimizedPreview() {
    RunBuddyTheme {
        BuddyMainCard(isExpanded = false)
    }
}