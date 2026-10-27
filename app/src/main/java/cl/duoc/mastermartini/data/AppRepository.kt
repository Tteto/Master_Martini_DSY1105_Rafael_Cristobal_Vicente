package cl.duoc.mastermartini.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

/** Unica puerta de acceso a Room y a la sesion local (SharedPreferences). */
class AppRepository(context: Context) {
    private val db = AppDatabase.get(context)
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun sessionEmail(): String? = prefs.getString("email", null)

    private fun setSession(email: String?) {
        val editor = prefs.edit()
        if (email == null) editor.remove("email") else editor.putString("email", email)
        editor.apply()
    }

    fun clearSession() = setSession(null)

    /** Devuelve false si el correo ya existe. */
    suspend fun register(name: String, email: String, phone: String, rut: String, pass: String): Boolean {
        val mail = email.trim().lowercase()
        if (db.userDao().find(mail) != null) return false
        db.userDao().insert(
            User(mail, name.trim(), phone.trim(), rut.trim().uppercase(), hash(pass))
        )
        setSession(mail)
        return true
    }

    suspend fun login(email: String, pass: String): User? {
        val user = db.userDao().find(email.trim().lowercase()) ?: return null
        if (user.passHash != hash(pass)) return null
        setSession(user.email)
        return user
    }

    fun observeUser(email: String): Flow<User?> = db.userDao().observe(email)
    suspend fun updateUser(user: User) = db.userDao().update(user)

    fun observeFavorites(email: String): Flow<List<Int>> = db.favoriteDao().observe(email)
    suspend fun addFavorite(email: String, id: Int) = db.favoriteDao().add(Favorite(email, id))
    suspend fun removeFavorite(email: String, id: Int) = db.favoriteDao().remove(Favorite(email, id))

    private fun hash(text: String): String =
        MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
