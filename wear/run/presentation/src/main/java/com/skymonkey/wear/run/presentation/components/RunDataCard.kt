package com.skymonkey.wear.run.presentation.components

import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import com.skymonkey.wear.run.presentation.R

@Composable
fun RunDataCard(
    @DrawableRes drawable: Int,
    value: String,
    modifier: Modifier = Modifier,
    drawableTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    valueTextColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier =
            modifier
                .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = drawable),
            contentDescription = null,
            tint = drawableTint
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
            text = value,
            color = valueTextColor,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun RunDataCardPreview() {
    RunBuddyTheme {
        RunDataCard(drawable = R.drawable.heart_rate_icon, value = "20km")
    }
}
