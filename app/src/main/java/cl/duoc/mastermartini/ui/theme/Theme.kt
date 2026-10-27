package cl.duoc.mastermartini.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Colores de referencia (reemplazar por los autorizados por Master Martini cuando se entreguen).
private val Colors = lightColorScheme(
    primary = Color(0xFF8B1E2D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD9),
    onPrimaryContainer = Color(0xFF410008),
    secondary = Color(0xFFB8860B),
    background = Color(0xFFFFFBF7),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F)
)

// Tipografia grande para facilitar la lectura a usuarios mayores.
private val Typo = Typography(
    bodyLarge = TextStyle(fontSize = 18.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontSize = 20.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontSize = 24.sp, lineHeight = 30.sp),
    headlineMedium = TextStyle(fontSize = 30.sp, lineHeight = 36.sp)
)

@Composable
fun MartiniTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = Typo, content = content)
}
