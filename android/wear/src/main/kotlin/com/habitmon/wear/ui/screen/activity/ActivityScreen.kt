package com.habitmon.wear.ui.screen.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.habitmon.shared.domain.ActivityGoals
import com.habitmon.wear.ui.theme.Accent
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.ScreenBg
import com.habitmon.wear.ui.theme.WaterBlue

private val SPANISH = Locale.forLanguageTag("es-ES")

@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = viewModel(factory = ActivityViewModel.Factory)
) {
    val activity by viewModel.todayActivity.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val steps = activity?.steps ?: 0
    val km = activity?.kilometers ?: 0.0

    Box(
        modifier = Modifier.fillMaxSize().background(ScreenBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "PROGRESO\nDIARIO",
                fontSize = 10.sp,
                color = Ink,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )

            if (selectedTab == 0) {
                Text(
                    text = "%,d".format(SPANISH, steps),
                    fontSize = 24.sp,
                    color = Accent,
                    textAlign = TextAlign.Center
                )

                ProgressBar(
                    progress = ActivityGoals.stepProgress(steps),
                    color = Accent
                )

                Text(text = "PASOS", fontSize = 7.sp, color = Muted)
            } else {
                Text(
                    text = "%.2f".format(SPANISH, km),
                    fontSize = 24.sp,
                    color = WaterBlue,
                    textAlign = TextAlign.Center
                )

                ProgressBar(
                    progress = ActivityGoals.distanceProgress(km),
                    color = WaterBlue
                )

                Text(text = "KILÓMETROS", fontSize = 7.sp, color = Muted)
            }

            Row {
                TabDot(active = selectedTab == 0, label = "Pasos", onClick = { selectedTab = 0 })
                TabDot(active = selectedTab == 1, label = "Kilómetros", onClick = { selectedTab = 1 })
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(Color(0x1F000000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(color)
        )
    }
}

@Composable
private fun TabDot(active: Boolean, label: String, onClick: () -> Unit) {
    // Zona táctil de 40dp alrededor de un punto de 10dp.
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (active) Ink else Ink.copy(alpha = 0.25f))
        )
    }
}
