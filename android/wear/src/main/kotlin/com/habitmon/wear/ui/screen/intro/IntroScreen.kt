package com.habitmon.wear.ui.screen.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.habitmon.wear.ui.component.SpritePlaceholder
import com.habitmon.wear.ui.theme.Accent
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.ScreenBg
import com.habitmon.wear.ui.theme.typeColor
import kotlinx.coroutines.launch

@Composable
fun IntroScreen(
    onComplete: () -> Unit,
    viewModel: IntroViewModel = viewModel(factory = IntroViewModel.Factory)
) {
    val companion by viewModel.companion.collectAsState()
    val scope = rememberCoroutineScope()

    val c = companion
    if (c == null) {
        Box(Modifier.fillMaxSize().background(ScreenBg), contentAlignment = Alignment.Center) {
            Text("Cargando...", fontSize = 9.sp, color = Muted)
        }
        return
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(ScreenBg),
        state = rememberScalingLazyListState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            SpritePlaceholder(
                initial = c.species.name.first().uppercase(),
                typeColor = typeColor(c.species.primaryType),
                size = 80.dp
            )
        }
        item {
            Text(
                text = "¡${c.species.name} quiere ser tu compañero! Juntos formaréis un gran equipo.",
                fontSize = 8.sp,
                color = Ink,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
        item {
            CompactChip(
                onClick = {
                    scope.launch {
                        viewModel.completeIntro()
                        onComplete()
                    }
                },
                label = { Text("CONTINUAR", fontSize = 9.sp, maxLines = 1) },
                colors = ChipDefaults.primaryChipColors(backgroundColor = Accent)
            )
        }
    }
}
