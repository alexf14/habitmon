package com.habitmon.wear.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.material.rememberScalingLazyListState
import com.habitmon.wear.BuildConfig
import com.habitmon.wear.ui.component.SpritePlaceholder
import com.habitmon.wear.ui.component.StatBar
import com.habitmon.wear.ui.theme.Accent
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.PlantGreen
import com.habitmon.wear.ui.theme.ScreenBg
import com.habitmon.wear.ui.theme.WaterBlue
import com.habitmon.wear.ui.theme.typeColor

private val Sky = Color(0xFFBFE3F5)
private val Grass = Color(0xFFA8D8A0)

@Composable
fun HomeScreen(
    onNavigateToPokedex: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToDebug: () -> Unit,
    onNoCompanion: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val state by viewModel.state.collectAsState()
    var showInfo by remember { mutableStateOf(false) }

    val current = state
    if (current is HomeState.NoCompanion) {
        LaunchedEffect(Unit) { onNoCompanion() }
    }
    if (current !is HomeState.Ready) {
        Box(Modifier.fillMaxSize().background(Sky), contentAlignment = Alignment.Center) {
            Text("Cargando...", fontSize = 9.sp, color = Ink)
        }
        return
    }
    val data = current.data

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0f to Sky, 0.55f to Sky, 0.55f to Grass, 1f to Grass)),
        state = rememberScalingLazyListState(initialCenterItemIndex = 1),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(data.displayName.uppercase(), fontSize = 10.sp, color = Ink, textAlign = TextAlign.Center)
                Text("Nv. ${data.companion.level}", fontSize = 8.sp, color = Muted)
            }
        }
        item {
            SpritePlaceholder(
                initial = data.species.name.first().uppercase(),
                typeColor = typeColor(data.species.primaryType),
                size = 80.dp
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .background(ScreenBg.copy(alpha = 0.9f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatBar("EXP", 0, 100, WaterBlue)
                StatBar("FEL", data.companion.happiness, 100, PlantGreen)
                StatBar("ENE", data.companion.energy, 100, Color(0xFFE8C547))
                StatBar("VIN", data.companion.bond, 100, Color(0xFFE86F9A))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NavChip("INFO", onClick = { showInfo = true })
                NavChip("POKÉDEX", onNavigateToPokedex)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NavChip("PASOS", onNavigateToActivity)
                if (BuildConfig.DEBUG) {
                    NavChip("DBG", onNavigateToDebug, background = Muted)
                }
            }
        }
    }

    Dialog(showDialog = showInfo, onDismissRequest = { showInfo = false }) {
        InfoDialog(data = data, onDismiss = { showInfo = false })
    }
}

@Composable
private fun NavChip(label: String, onClick: () -> Unit, background: Color = Accent) {
    CompactChip(
        onClick = onClick,
        label = { Text(label, fontSize = 8.sp, maxLines = 1) },
        colors = ChipDefaults.primaryChipColors(backgroundColor = background)
    )
}

@Composable
private fun InfoDialog(
    data: CompanionDisplayData,
    onDismiss: () -> Unit
) {
    Alert(
        title = {
            Text(
                text = data.species.name.uppercase(),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        },
    ) {
        item { InfoRow("Nº", "%03d".format(data.species.pokedexNumber)) }
        item { InfoRow("Tipo", data.species.typesDisplay) }
        item { InfoRow("Nivel", "${data.companion.level}") }
        item { InfoRow("EXP", "${data.companion.experience}") }
        item { InfoRow("Felicidad", "${data.companion.happiness}/100") }
        item { InfoRow("Energía", "${data.companion.energy}/100") }
        item { InfoRow("Vínculo", "${data.companion.bond}/100") }
        item {
            CompactChip(
                onClick = onDismiss,
                label = { Text("CERRAR", fontSize = 8.sp) },
                colors = ChipDefaults.primaryChipColors(backgroundColor = Accent)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 8.sp, color = Muted)
        Text(text = value, fontSize = 8.sp)
    }
}
