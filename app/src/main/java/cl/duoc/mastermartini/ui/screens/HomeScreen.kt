@file:OptIn(ExperimentalMaterial3Api::class)

package cl.duoc.mastermartini.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mastermartini.data.ContentData
import cl.duoc.mastermartini.ui.components.ContentCard
import cl.duoc.mastermartini.util.Haptics
import cl.duoc.mastermartini.viewmodel.ContentViewModel

@Composable
fun HomeScreen(
    vm: ContentViewModel,
    userName: String,
    onlyFavorites: Boolean,
    onOpen: (Int) -> Unit
) {
    val all by vm.items.collectAsState()
    val favs by vm.favorites.collectAsState()
    val category by vm.category.collectAsState()
    val query by vm.query.collectAsState()
    val ctx = LocalContext.current
    val list = if (onlyFavorites) all.filter { it.id in favs } else all

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                if (onlyFavorites) "Mis favoritos" else "Hola, $userName 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { vm.query.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar recetas, videos o cursos") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { vm.query.value = "" }) {
                            Icon(Icons.Filled.Close, "Borrar búsqueda")
                        }
                    }
                }
            )
        }
        item {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContentData.types.forEach { t ->
                    FilterChip(
                        selected = category == t,
                        onClick = { vm.category.value = t },
                        label = { Text(t, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp)) }
                    )
                }
            }
        }
        if (list.isEmpty()) {
            item {
                Text(
                    if (onlyFavorites) "Aún no tienes favoritos. Toca el corazón en un contenido."
                    else "No encontramos contenidos con ese filtro."
                )
            }
        }
        items(list, key = { it.id }) { c ->
            ContentCard(
                content = c,
                isFav = c.id in favs,
                onClick = { onOpen(c.id) },
                onFav = {
                    if (c.id !in favs) Haptics.vibrate(ctx, 40)
                    vm.toggleFavorite(c.id)
                }
            )
        }
    }
}
