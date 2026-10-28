package com.habitmon.wear.ui.screen.pokedex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import com.habitmon.shared.data.db.entity.PokedexEntryWithSpecies
import com.habitmon.shared.data.db.entity.PokedexStatus
import com.habitmon.wear.ui.theme.Accent
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.ScreenBg

@Composable
fun PokedexScreen(
    onSpeciesClick: (Int) -> Unit,
    viewModel: PokedexViewModel = viewModel(factory = PokedexViewModel.Factory)
) {
    val entries by viewModel.entries.collectAsState()

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        state = rememberScalingLazyListState(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(
                text = "POKÉDEX",
                fontSize = 12.sp,
                color = Accent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        items(entries) { entry ->
            PokedexRow(
                entry = entry,
                onClick = { onSpeciesClick(entry.species.pokedexNumber) }
            )
        }
    }
}

@Composable
private fun PokedexRow(
    entry: PokedexEntryWithSpecies,
    onClick: () -> Unit
) {
    val isKnown = entry.entry.status != PokedexStatus.UNKNOWN

    Chip(
        onClick = onClick,
        enabled = isKnown,
        modifier = Modifier.fillMaxWidth(),
        colors = ChipDefaults.secondaryChipColors(),
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = String.format("#%03d", entry.species.pokedexNumber),
                    fontSize = 8.sp,
                    color = Muted
                )

                if (isKnown) {
                    Column {
                        Text(
                            text = entry.species.name.uppercase(),
                            fontSize = 8.sp,
                            color = Ink
                        )
                        Text(
                            text = entry.species.primaryType.uppercase(),
                            fontSize = 6.sp,
                            color = Muted
                        )
                    }
                } else {
                    Text(
                        text = "???",
                        fontSize = 8.sp,
                        color = Muted
                    )
                }

                if (entry.entry.status == PokedexStatus.UNLOCKED) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Accent)
                    )
                }
            }
        }
    )
}
