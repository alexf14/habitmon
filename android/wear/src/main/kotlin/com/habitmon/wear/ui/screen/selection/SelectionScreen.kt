package com.habitmon.wear.ui.screen.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.habitmon.shared.data.db.entity.PokemonSpecies
import com.habitmon.wear.ui.component.SpritePlaceholder
import com.habitmon.wear.ui.theme.Ink
import com.habitmon.wear.ui.theme.Muted
import com.habitmon.wear.ui.theme.ScreenBg
import com.habitmon.wear.ui.theme.typeColor
import kotlinx.coroutines.launch

@Composable
fun SelectionScreen(
    onSelected: () -> Unit,
    viewModel: SelectionViewModel = viewModel(factory = SelectionViewModel.Factory)
) {
    val starters by viewModel.starters.collectAsState()
    val scope = rememberCoroutineScope()

    if (starters.isEmpty()) {
        Box(Modifier.fillMaxSize().background(ScreenBg), contentAlignment = Alignment.Center) {
            Text("Cargando...", fontSize = 9.sp, color = Ink)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { starters.size })

    Box(
        modifier = Modifier.fillMaxSize().background(ScreenBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "ELIGE TU\nCOMPAÑERO",
                fontSize = 10.sp,
                color = Ink,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val species = starters[page]
                StarterCard(species = species)
            }

            PageIndicator(
                pageCount = starters.size,
                currentPage = pagerState.currentPage
            )

            Button(onClick = {
                scope.launch {
                    val species = starters[pagerState.currentPage]
                    viewModel.chooseStarter(species.pokedexNumber)
                    onSelected()
                }
            }) {
                Text(text = "ELEGIR", fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun StarterCard(species: PokemonSpecies) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SpritePlaceholder(
            initial = species.name.first().uppercase(),
            typeColor = typeColor(species.primaryType),
            size = 80.dp
        )
        Text(
            text = species.name.uppercase(),
            fontSize = 10.sp,
            color = typeColor(species.primaryType)
        )
        Text(
            text = species.primaryType.uppercase(),
            fontSize = 7.sp,
            color = Muted
        )
    }
}

@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(pageCount) { i ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (i == currentPage) Ink else Ink.copy(alpha = 0.25f))
            )
        }
    }
}
