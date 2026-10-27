package cl.duoc.mastermartini.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.mastermartini.ui.components.BigButton
import cl.duoc.mastermartini.ui.components.ErrorLine
import cl.duoc.mastermartini.ui.components.ValidatedField
import cl.duoc.mastermartini.util.Haptics
import cl.duoc.mastermartini.viewmodel.AuthViewModel

@Composable
fun LoginScreen(vm: AuthViewModel, onLogged: () -> Unit, onRegister: () -> Unit) {
    val s = vm.login
    val ctx = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("🍸 Martini Academy", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Recetas, videos y cursos para tu negocio gastronómico.")
        Spacer(Modifier.height(8.dp))

        ValidatedField("Correo", s.email, { v -> vm.updateLogin { it.copy(email = v) } },
            Icons.Filled.Email, s.emailError, keyboardType = KeyboardType.Email)
        ValidatedField("Contraseña", s.pass, { v -> vm.updateLogin { it.copy(pass = v) } },
            Icons.Filled.Lock, s.passError, isPassword = true)

        AnimatedVisibility(s.authError != null, enter = scaleIn() + fadeIn(), exit = fadeOut()) {
            ErrorLine(s.authError)
        }

        BigButton("Ingresar", onClick = {
            vm.submitLogin(onSuccess = onLogged, onFail = { Haptics.vibrate(ctx, 120) })
        })
        BigButton("Crear cuenta", onClick = onRegister, outlined = true)
    }
}

@Composable
fun RegisterScreen(vm: AuthViewModel, onRegistered: () -> Unit, onBack: () -> Unit) {
    val s = vm.register
    val ctx = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Usa datos ficticios: es un prototipo académico.")

        ValidatedField("Nombre completo", s.name, { v -> vm.updateRegister { it.copy(name = v) } },
            Icons.Filled.Person, s.nameError)
        ValidatedField("Correo", s.email, { v -> vm.updateRegister { it.copy(email = v) } },
            Icons.Filled.Email, s.emailError, keyboardType = KeyboardType.Email)
        ValidatedField("Teléfono (ej: 912345678)", s.phone, { v -> vm.updateRegister { it.copy(phone = v) } },
            Icons.Filled.Phone, s.phoneError, keyboardType = KeyboardType.Phone)
        ValidatedField("RUT empresa (ej: 76123456-0)", s.rut, { v -> vm.updateRegister { it.copy(rut = v) } },
            Icons.Filled.Business, s.rutError)
        ValidatedField("Contraseña", s.pass, { v -> vm.updateRegister { it.copy(pass = v) } },
            Icons.Filled.Lock, s.passError, isPassword = true)
        ValidatedField("Repetir contraseña", s.pass2, { v -> vm.updateRegister { it.copy(pass2 = v) } },
            Icons.Filled.Badge, s.pass2Error, isPassword = true)

        Spacer(Modifier.height(6.dp))
        BigButton("Registrarme", onClick = {
            vm.submitRegister(onSuccess = onRegistered, onFail = { Haptics.vibrate(ctx, 120) })
        })
        BigButton("Volver", onClick = onBack, outlined = true)
    }
}
