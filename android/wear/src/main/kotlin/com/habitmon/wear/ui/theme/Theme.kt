package com.habitmon.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Typography

val Accent = Color(0xFFE3350D)
val ScreenBg = Color(0xFFF6F3E4)
val Ink = Color(0xFF1D2230)
val Muted = Color(0xFF6B6F7A)

val PlantGreen = Color(0xFF4CAF50)
val FireOrange = Color(0xFFF08030)
val WaterBlue = Color(0xFF4F86E8)

/** Color de un tipo Pokémon tal como aparece en el catálogo ("Fuego", "Eléctrico"...). */
fun typeColor(type: String?): Color = when (type?.lowercase()) {
    "planta" -> PlantGreen
    "fuego" -> FireOrange
    "agua" -> WaterBlue
    "eléctrico" -> Color(0xFFE0B520)
    "hielo" -> Color(0xFF6FC8C8)
    "lucha" -> Color(0xFFC03028)
    "veneno" -> Color(0xFFA040A0)
    "tierra" -> Color(0xFFC8A048)
    "volador" -> Color(0xFF8C7CD8)
    "psíquico" -> Color(0xFFF85888)
    "bicho" -> Color(0xFF98A820)
    "roca" -> Color(0xFFB8A038)
    "fantasma" -> Color(0xFF705898)
    "dragón" -> Color(0xFF7038F8)
    "acero" -> Color(0xFF9898B8)
    "hada" -> Color(0xFFE08EB0)
    else -> Muted
}

val Gb0 = Color(0xFF0F380F)
val Gb1 = Color(0xFF306230)
val Gb2 = Color(0xFF8BAC0F)
val Gb3 = Color(0xFF9BBC0F)

val HabitMonColors = Colors(
    primary = Accent,
    primaryVariant = Color(0xFFB82A0A),
    secondary = PlantGreen,
    secondaryVariant = Color(0xFF388E3C),
    background = ScreenBg,
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Muted,
    onError = Color.Black
)

val HabitMonTypography = Typography(
    defaultFontFamily = FontFamily.Monospace
)

@Composable
fun HabitMonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = HabitMonColors,
        typography = HabitMonTypography,
        content = content
    )
}
