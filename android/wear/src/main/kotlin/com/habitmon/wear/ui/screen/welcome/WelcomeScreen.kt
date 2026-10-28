package com.habitmon.wear.ui.screen.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.habitmon.wear.ui.theme.Accent
import com.habitmon.wear.ui.theme.Gb0
import com.habitmon.wear.ui.theme.Gb1
import com.habitmon.wear.ui.theme.Gb2
import com.habitmon.wear.ui.theme.Gb3

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onResumeIntro: () -> Unit,
    onResumeHome: () -> Unit,
    viewModel: WelcomeViewModel = viewModel(factory = WelcomeViewModel.Factory)
) {
    val state by viewModel.companionState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            CompanionCheckState.NoCompanion -> { /* stay */ }
            CompanionCheckState.NeedsIntro -> onResumeIntro()
            CompanionCheckState.Ready -> onResumeHome()
            CompanionCheckState.Loading -> { /* wait */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gb3),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Pokeball(modifier = Modifier.size(36.dp))
                Text(
                    text = "HABIT\nMON",
                    fontSize = 20.sp,
                    color = Gb0,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Start
                )
            }

            Text(
                text = "¡Tu compañero Pokémon\nte espera!",
                fontSize = 8.sp,
                color = Gb0,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(backgroundColor = Gb1)
            ) {
                Text(text = "COMENZAR", fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun Pokeball(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(3.dp, Gb0, CircleShape)
            .background(Gb2),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Gb3)
                .border(2.dp, Gb0, CircleShape)
        )
    }
}

enum class CompanionCheckState {
    Loading, NoCompanion, NeedsIntro, Ready
}
