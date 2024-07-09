package com.skymonkey.run.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme

@Composable
fun SettingSwitch(
    text: String,
    isChecked: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically),
            text = text,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            checked = isChecked,
            onCheckedChange = {
                onAction()
            }
        )
    }
}


@Preview
@Composable
private fun SettingSwitchPreview() {
    RunBuddyTheme {
        SettingSwitch(
            text = "test title",
            isChecked = true,
            onAction = {}
        )
    }
}
