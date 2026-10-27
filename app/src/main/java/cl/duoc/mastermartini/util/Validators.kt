package cl.duoc.mastermartini.util

/** Toda la logica de validacion vive aqui (desacoplada de la UI). Devuelve null si es valido. */
object Validators {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val phoneRegex = Regex("^(\\+?56)?9\\d{8}$")

    fun name(v: String): String? = when {
        v.isBlank() -> "Ingresa tu nombre"
        v.trim().length < 3 -> "Mínimo 3 caracteres"
        !v.all { it.isLetter() || it == ' ' } -> "Solo letras y espacios"
        else -> null
    }

    fun email(v: String): String? = when {
        v.isBlank() -> "Ingresa tu correo"
        !emailRegex.matches(v.trim()) -> "Correo no válido (ej: nombre@correo.cl)"
        else -> null
    }

    fun phone(v: String): String? {
        val clean = v.replace(" ", "")
        return when {
            clean.isBlank() -> "Ingresa tu teléfono"
            !phoneRegex.matches(clean) -> "Teléfono chileno no válido (ej: 912345678)"
            else -> null
        }
    }

    /** Valida RUT con modulo 11 (se usan RUT ficticios en la app). */
    fun rut(v: String): String? {
        val clean = v.replace(".", "").replace("-", "").uppercase()
        if (clean.isBlank()) return "Ingresa el RUT de la empresa"
        if (clean.length !in 8..9) return "RUT no válido (ej: 76123456-0)"
        val body = clean.dropLast(1)
        if (!body.all { it.isDigit() }) return "RUT no válido (ej: 76123456-0)"
        var sum = 0
        var mult = 2
        for (d in body.reversed()) {
            sum += d.digitToInt() * mult
            mult = if (mult == 7) 2 else mult + 1
        }
        val r = 11 - sum % 11
        val expected = when (r) {
            11 -> '0'
            10 -> 'K'
            else -> r.digitToChar()
        }
        return if (expected == clean.last()) null else "RUT no válido, revisa el dígito verificador"
    }

    fun password(v: String): String? = when {
        v.isEmpty() -> "Ingresa una contraseña"
        v.length < 6 -> "Mínimo 6 caracteres"
        !v.any { it.isLetter() } || !v.any { it.isDigit() } -> "Debe tener letras y números"
        else -> null
    }

    fun confirm(pass: String, pass2: String): String? = when {
        pass2.isEmpty() -> "Repite la contraseña"
        pass != pass2 -> "Las contraseñas no coinciden"
        else -> null
    }

    fun required(v: String, msg: String): String? = if (v.isBlank()) msg else null
}
