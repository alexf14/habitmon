package com.habitmon.wear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.PlantGreen
import com.habitmon.wear.ui.theme.ScreenBg

@Composable
fun StatBar(
    label: String,
    value: Int,
    maxValue: Int = 100,
    fillColor: Color = PlantGreen,
    modifier: Modifier = Modifier
) {
    val fraction = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    Row(
        modifier = modifier.fillMaxWidth().height(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 7.sp,
            color = Ink,
            modifier = Modifier.size(width = 30.dp, height = 14.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(Color(0x1F000000))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(fillColor)
            )
        }
    }
}

@Composable
fun SpritePlaceholder(
    initial: String,
    typeColor: Color = Muted,
    size: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(typeColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 28.sp,
            color = typeColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PixelBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ScreenBg,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.background(backgroundColor)
    ) {
        content()
    }
}
