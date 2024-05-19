package com.skymonkey.auth.presentation.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skymonkey.auth.presentation.R
import com.skymonkey.core.presentation.designsystem.LogoIcon
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.core.presentation.designsystem.components.GradientBackground

@Composable
fun IntroScreenRoot(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    IntroScreen(state = "", onAction = {
        when(it) {
            IntroAction.OnSignInClick -> onSignUpClick()
            IntroAction.OnSignUpClick -> onSignInClick()
        }
    })
}

@Composable
fun IntroScreen(
    state: String,
    onAction: (IntroAction) -> Unit,
    modifier: Modifier = Modifier
) {
    GradientBackground {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // box takes up all remaining space in column
            contentAlignment = Alignment.Center
        ) {
            RunbuddyLogoVertical()
        }
    }
}

@Composable
private fun RunbuddyLogoVertical(
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = LogoIcon,
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.runbuddy),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground

        )
    }
}

@Preview
@Composable
fun IntroScreenPreview() {
    RunBuddyTheme {
        IntroScreen(state = "", onAction = {})
    }
}