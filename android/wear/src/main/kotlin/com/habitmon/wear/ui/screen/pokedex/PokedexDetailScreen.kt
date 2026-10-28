package com.habitmon.wear.ui.screen.pokedex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import com.habitmon.shared.data.db.entity.PokedexStatus
import com.habitmon.wear.ui.component.SpritePlaceholder
import com.habitmon.wear.ui.theme.Accent
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.ScreenBg
import com.habitmon.wear.ui.theme.typeColor

@Composable
fun PokedexDetailScreen(
    speciesId: Int,
    onCompanionChanged: () -> Unit,
    viewModel: PokedexViewModel = viewModel(factory = PokedexViewModel.Factory)
) {
    val detailFlow = remember(speciesId) { viewModel.getSpeciesDetail(speciesId) }
    val detail by detailFlow.collectAsState(initial = null)

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        state = rememberScalingLazyListState()
    ) {
        detail?.let { d ->
            item {
                SpritePlaceholder(
                    initial = d.species.name.first().uppercase(),
                    typeColor = typeColor(d.species.primaryType),
                    size = 80.dp
                )
            }

            item {
                Text(
                    text = d.species.name.uppercase(),
                    fontSize = 12.sp,
                    color = Ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "#${String.format("%03d", d.species.pokedexNumber)}",
                    fontSize = 9.sp,
                    color = Accent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                DetailRow("Tipo", d.species.typesDisplay)
            }

            if (d.species.genus.isNotBlank()) {
                item { DetailRow("Especie", d.species.genus) }
            }

            item {
                DetailRow("Altura", "%.1f m".format(Locale.forLanguageTag("es-ES"), d.species.heightM))
            }

            item {
                DetailRow("Peso", "%.1f kg".format(Locale.forLanguageTag("es-ES"), d.species.weightKg))
            }

            if (d.species.description.isNotBlank()) {
                item {
                    Text(
                        text = d.species.description,
                        fontSize = 7.sp,
                        color = Ink,
                        lineHeight = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (d.entry.status == PokedexStatus.UNLOCKED) {
                item {
                    Button(
                        onClick = { viewModel.setActiveCompanion(d.species.pokedexNumber, onCompanionChanged) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("ELEGIR", fontSize = 8.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 7.sp, color = Muted)
        Text(text = value, fontSize = 7.sp, color = Ink)
    }
}
