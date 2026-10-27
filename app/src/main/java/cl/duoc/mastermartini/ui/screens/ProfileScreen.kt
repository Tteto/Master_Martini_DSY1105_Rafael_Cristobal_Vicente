package cl.duoc.mastermartini.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.mastermartini.ui.components.BigButton
import cl.duoc.mastermartini.ui.components.CheckGroup
import cl.duoc.mastermartini.ui.components.ErrorLine
import cl.duoc.mastermartini.ui.components.RadioGroup
import cl.duoc.mastermartini.util.Haptics
import cl.duoc.mastermartini.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

private val businessTypes = listOf("Panadería", "Pastelería", "Chocolatería", "Restaurante / Casino", "Heladería", "Otro")
private val businessSizes = listOf("Micro (1-5 personas)", "Pequeño (6-20 personas)", "Mediano (21 o más)")
private val interestOptions = listOf("Recetas simples", "Videos técnicos", "Cursos de especialidad", "Eventos y degustaciones")

@Composable
fun ProfileScreen(vm: AuthViewModel, onLogout: () -> Unit) {
    val user by vm.user.collectAsState()
    val ctx = LocalContext.current
    val s = vm.survey
    var permMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user?.email) { user?.let { vm.loadSurvey(it) } }
    LaunchedEffect(vm.surveySaved) {
        if (vm.surveySaved) { delay(2500); vm.clearSaved() }
    }

    // Recurso nativo 1: camara (con permiso en tiempo de ejecucion)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) vm.savePhoto(bmp)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) { permMsg = null; cameraLauncher.launch(null) }
        else permMsg = "Sin permiso de cámara no podemos tomar la foto. Puedes activarlo en Ajustes."
    }

    val photo = remember(user?.photoPath) {
        user?.photoPath?.takeIf { it.isNotBlank() }?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (photo != null) {
                Image(photo, "Foto de perfil", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Filled.Person, "Sin foto", Modifier.size(64.dp))
            }
        }
        Text(user?.name ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(user?.email ?: "")

        BigButton("Tomar foto de perfil", icon = Icons.Filled.CameraAlt, onClick = {
            val granted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            if (granted) cameraLauncher.launch(null) else permissionLauncher.launch(Manifest.permission.CAMERA)
        })
        AnimatedVisibility(permMsg != null, enter = fadeIn(), exit = fadeOut()) { ErrorLine(permMsg) }

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Encuesta de intereses", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            RadioGroup("Tipo de negocio", businessTypes, s.businessType, s.typeError) { v ->
                vm.updateSurvey { it.copy(businessType = v) }
            }
            RadioGroup("Tamaño aproximado", businessSizes, s.size, s.sizeError) { v ->
                vm.updateSurvey { it.copy(size = v) }
            }
            CheckGroup("¿Qué contenido te interesa?", interestOptions, s.interests, s.interestsError) { v ->
                vm.updateSurvey { st ->
                    st.copy(interests = if (v in st.interests) st.interests - v else st.interests + v)
                }
            }
        }

        // Recurso nativo 2: vibracion al guardar
        BigButton("Guardar preferencias", icon = Icons.Filled.Save, onClick = {
            vm.saveSurvey(onSaved = { Haptics.vibrate(ctx, 150) })
        })

        AnimatedVisibility(vm.surveySaved, enter = scaleIn() + fadeIn(), exit = fadeOut()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF2E7D32))
                    Spacer(Modifier.width(8.dp))
                    Text("¡Preferencias guardadas!")
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        BigButton("Cerrar sesión", icon = Icons.Filled.ExitToApp, outlined = true, onClick = {
            vm.logout(); onLogout()
        })
    }
}
