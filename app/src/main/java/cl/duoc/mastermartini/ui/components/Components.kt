@file:OptIn(ExperimentalMaterial3Api::class)

package cl.duoc.mastermartini.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mastermartini.data.Content

private val OkGreen = Color(0xFF2E7D32)

/** Campo con icono inicial, icono de estado (error / valido) y mensaje animado. */
@Composable
fun ValidatedField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    icon: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    var lastError by remember { mutableStateOf("") }
    LaunchedEffect(error) { if (error != null) lastError = error }
    val state = when {
        error != null -> 1
        value.isNotEmpty() -> 2
        else -> 0
    }

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        leadingIcon = { Icon(icon, contentDescription = null) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Mostrar u ocultar contraseña"
                    )
                }
            } else {
                Crossfade(targetState = state, label = "estado") { s ->
                    when (s) {
                        1 -> Icon(Icons.Filled.Error, "Campo con error", tint = MaterialTheme.colorScheme.error)
                        2 -> Icon(Icons.Filled.CheckCircle, "Campo válido", tint = OkGreen)
                        else -> Spacer(Modifier.size(24.dp))
                    }
                }
            }
        },
        isError = error != null,
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        supportingText = {
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(error ?: lastError)
                }
            }
        },
        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
    )
}

/** Boton grande con animacion de pulsado. */
@Composable
fun BigButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    outlined: Boolean = false
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "press")
    val m = modifier
        .fillMaxWidth()
        .heightIn(min = 56.dp)
        .graphicsLayer { scaleX = scale; scaleY = scale }
    val content: @Composable RowScope.() -> Unit = {
        if (icon != null) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontSize = 18.sp)
    }
    if (outlined) OutlinedButton(onClick = onClick, modifier = m, interactionSource = source, content = content)
    else Button(onClick = onClick, modifier = m, interactionSource = source, content = content)
}

/** Grupo de opciones de seleccion unica. */
@Composable
fun RadioGroup(
    title: String,
    options: List<String>,
    selected: String,
    error: String?,
    onSelect: (String) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        options.forEach { o ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .selectable(selected = o == selected, onClick = { onSelect(o) }, role = Role.RadioButton),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = o == selected, onClick = null)
                Spacer(Modifier.width(12.dp))
                Text(o)
            }
        }
        ErrorLine(error)
    }
}

/** Grupo de opciones de seleccion multiple. */
@Composable
fun CheckGroup(
    title: String,
    options: List<String>,
    selected: Set<String>,
    error: String?,
    onToggle: (String) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        options.forEach { o ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(value = o in selected, onValueChange = { onToggle(o) }, role = Role.Checkbox),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = o in selected, onCheckedChange = null)
                Spacer(Modifier.width(12.dp))
                Text(o)
            }
        }
        ErrorLine(error)
    }
}

@Composable
fun ErrorLine(error: String?) {
    var last by remember { mutableStateOf("") }
    LaunchedEffect(error) { if (error != null) last = error }
    AnimatedVisibility(
        visible = error != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(4.dp))
            Text(error ?: last, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
    }
}

@Composable
fun FavButton(isFav: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isFav) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fav"
    )
    IconButton(onClick = onClick, modifier = Modifier.size(56.dp)) {
        Icon(
            if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFav) "Quitar de favoritos" else "Agregar a favoritos",
            tint = if (isFav) Color(0xFFD32F2F) else LocalContentColor.current,
            modifier = Modifier.scale(scale)
        )
    }
}

@Composable
fun ContentCard(content: Content, isFav: Boolean, onClick: () -> Unit, onFav: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) { Text(content.emoji, fontSize = 32.sp) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(content.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${content.type} · ${content.duration}", color = MaterialTheme.colorScheme.secondary)
            }
            FavButton(isFav, onFav)
        }
    }
}
