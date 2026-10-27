package cl.duoc.mastermartini.viewmodel

import cl.duoc.mastermartini.util.Validators

/** Estados de formulario: los errores se calculan aqui, la UI solo los muestra. */

data class LoginState(
    val email: String = "",
    val pass: String = "",
    val submitted: Boolean = false,
    val authError: String? = null
) {
    val emailError get() = if (email.isNotEmpty() || submitted) Validators.email(email) else null
    val passError get() = if (pass.isNotEmpty() || submitted) Validators.required(pass, "Ingresa tu contraseña") else null
    val valid get() = Validators.email(email) == null && pass.isNotEmpty()
}

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val rut: String = "",
    val pass: String = "",
    val pass2: String = "",
    val submitted: Boolean = false,
    val emailTaken: Boolean = false
) {
    private fun show(v: String) = v.isNotEmpty() || submitted
    val nameError get() = if (show(name)) Validators.name(name) else null
    val emailError get() = when {
        !show(email) -> null
        emailTaken -> "Este correo ya está registrado"
        else -> Validators.email(email)
    }
    val phoneError get() = if (show(phone)) Validators.phone(phone) else null
    val rutError get() = if (show(rut)) Validators.rut(rut) else null
    val passError get() = if (show(pass)) Validators.password(pass) else null
    val pass2Error get() = if (show(pass2)) Validators.confirm(pass, pass2) else null
    val valid get() = Validators.name(name) == null && Validators.email(email) == null &&
        Validators.phone(phone) == null && Validators.rut(rut) == null &&
        Validators.password(pass) == null && Validators.confirm(pass, pass2) == null
}

data class SurveyState(
    val businessType: String = "",
    val size: String = "",
    val interests: Set<String> = emptySet(),
    val submitted: Boolean = false
) {
    val typeError get() = if (submitted) Validators.required(businessType, "Elige el tipo de negocio") else null
    val sizeError get() = if (submitted) Validators.required(size, "Elige el tamaño del negocio") else null
    val interestsError get() = if (submitted && interests.isEmpty()) "Elige al menos un interés" else null
    val valid get() = businessType.isNotBlank() && size.isNotBlank() && interests.isNotEmpty()
}
