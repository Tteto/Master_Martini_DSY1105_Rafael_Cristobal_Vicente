package cl.duoc.mastermartini.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mastermartini.ui.components.BigButton
import cl.duoc.mastermartini.ui.components.FavButton
import cl.duoc.mastermartini.util.Haptics
import cl.duoc.mastermartini.viewmodel.ContentViewModel

@Composable
fun DetailScreen(id: Int, vm: ContentViewModel, onBack: () -> Unit) {
    val content = vm.find(id)
    val favs by vm.favorites.collectAsState()
    val ctx = LocalContext.current
    var showSteps by remember { mutableStateOf(false) }

    if (content == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Contenido no encontrado") }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Filled.ArrowBack, "Volver")
            }
            Text("${content.type} · ${content.duration}", modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.secondary)
            val isFav = content.id in favs
            FavButton(isFav) {
                if (!isFav) Haptics.vibrate(ctx, 40)
                vm.toggleFavorite(content.id)
            }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(content.emoji, fontSize = 96.sp)
        }
        Text(content.title, style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(content.description)
        Spacer(Modifier.height(4.dp))
        BigButton(
            text = if (showSteps) "Ocultar pasos" else "Ver pasos / contenido",
            onClick = { showSteps = !showSteps },
            icon = Icons.Filled.PlayArrow
        )
        AnimatedVisibility(showSteps, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                content.steps.forEachIndexed { i, s ->
                    Text("${i + 1}. $s", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
