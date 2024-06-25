package com.skymonkey.run.presentation.run_overview.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.run.presentation.run_overview.model.RunCellData

@Composable
fun DataGridCell(
    run: RunCellData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = run.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = run.value,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
private fun DataGridCellPreview() {
    RunBuddyTheme {
        DataGridCell(run = RunCellData("test","123"))
    }
}
