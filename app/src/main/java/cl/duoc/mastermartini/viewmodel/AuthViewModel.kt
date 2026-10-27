package cl.duoc.mastermartini.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.AppRepository
import cl.duoc.mastermartini.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AppRepository(app)

    private val _session = MutableStateFlow(repo.sessionEmail())
    val session: StateFlow<String?> = _session

    @OptIn(ExperimentalCoroutinesApi::class)
    val user: StateFlow<User?> = _session
        .flatMapLatest { email -> if (email == null) flowOf(null) else repo.observeUser(email) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    var login by mutableStateOf(LoginState()); private set
    var register by mutableStateOf(RegisterState()); private set
    var survey by mutableStateOf(SurveyState()); private set
    var surveySaved by mutableStateOf(false); private set

    // ---------- Login ----------
    fun updateLogin(change: (LoginState) -> LoginState) {
        login = change(login).copy(authError = null)
    }

    fun submitLogin(onSuccess: () -> Unit, onFail: () -> Unit) {
        login = login.copy(submitted = true)
        if (!login.valid) { onFail(); return }
        viewModelScope.launch {
            val u = repo.login(login.email, login.pass)
            if (u == null) {
                login = login.copy(authError = "Correo o contraseña incorrectos")
                onFail()
            } else {
                _session.value = u.email
                login = LoginState()
                onSuccess()
            }
        }
    }

    // ---------- Registro ----------
    fun updateRegister(change: (RegisterState) -> RegisterState) {
        register = change(register).copy(emailTaken = false)
    }

    fun submitRegister(onSuccess: () -> Unit, onFail: () -> Unit) {
        register = register.copy(submitted = true)
        if (!register.valid) { onFail(); return }
        viewModelScope.launch {
            val r = register
            val ok = repo.register(r.name, r.email, r.phone, r.rut, r.pass)
            if (!ok) {
                register = register.copy(emailTaken = true)
                onFail()
            } else {
                _session.value = r.email.trim().lowercase()
                register = RegisterState()
                onSuccess()
            }
        }
    }

    // ---------- Encuesta de intereses ----------
    fun loadSurvey(u: User) {
        survey = SurveyState(
            businessType = u.businessType,
            size = u.businessSize,
            interests = u.interests.split(",").filter { it.isNotBlank() }.toSet()
        )
    }

    fun updateSurvey(change: (SurveyState) -> SurveyState) {
        survey = change(survey)
    }

    fun saveSurvey(onSaved: () -> Unit) {
        survey = survey.copy(submitted = true)
        if (!survey.valid) return
        val u = user.value ?: return
        viewModelScope.launch {
            repo.updateUser(
                u.copy(
                    businessType = survey.businessType,
                    businessSize = survey.size,
                    interests = survey.interests.joinToString(",")
                )
            )
            surveySaved = true
            onSaved()
        }
    }

    fun clearSaved() { surveySaved = false }

    // ---------- Foto de perfil (camara) ----------
    fun savePhoto(bitmap: Bitmap) {
        val u = user.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val dir = getApplication<Application>().filesDir
            val file = File(dir, "profile_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            if (u.photoPath.isNotBlank()) File(u.photoPath).delete()
            repo.updateUser(u.copy(photoPath = file.path))
        }
    }

    fun logout() {
        repo.clearSession()
        _session.value = null
        survey = SurveyState()
        surveySaved = false
    }
}
