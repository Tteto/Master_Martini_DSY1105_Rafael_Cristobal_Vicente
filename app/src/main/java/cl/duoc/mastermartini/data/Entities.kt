package cl.duoc.mastermartini.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val phone: String,
    val rut: String,
    val passHash: String,
    val businessType: String = "",
    val businessSize: String = "",
    val interests: String = "",
    val photoPath: String = ""
)

@Entity(tableName = "favorites", primaryKeys = ["email", "contentId"])
data class Favorite(val email: String, val contentId: Int)
