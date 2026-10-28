package com.habitmon.wear.ui.screen.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.habitmon.wear.ui.theme.Accent
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.ScreenBg

@Composable
fun DebugScreen(
    viewModel: DebugViewModel = viewModel(factory = DebugViewModel.Factory)
) {
    val stats by viewModel.stats.collectAsState()
    val lastResult by viewModel.lastResult.collectAsState()

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        state = rememberScalingLazyListState(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                text = "MODO DEBUG",
                fontSize = 12.sp,
                color = Accent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        item {
            Text(
                text = stats,
                fontSize = 7.sp,
                color = Muted,
                lineHeight = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (lastResult.isNotEmpty()) {
            item {
                Text(
                    text = lastResult,
                    fontSize = 7.sp,
                    color = if (lastResult.startsWith("ERROR")) Accent else Ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item { DebugButton("+ 1000 PASOS") { viewModel.addSteps(1000) } }
        item { DebugButton("+ 5000 PASOS") { viewModel.addSteps(5000) } }
        item { DebugButton("+ 1 KM") { viewModel.addKilometers(1.0) } }
        item { DebugButton("DESBLOQUEAR #004") { viewModel.unlockPokemon(4) } }
        item { DebugButton("DESBLOQUEAR #007") { viewModel.unlockPokemon(7) } }
        item { DebugButton("COMPAÑERO #025") { viewModel.setCompanion(25) } }

        item {
            Button(
                onClick = { viewModel.resetAll() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Accent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("RESET COMPLETO", fontSize = 8.sp)
            }
        }
    }
}

@Composable
private fun DebugButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.secondaryButtonColors(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text, fontSize = 8.sp, color = Ink)
    }
}
