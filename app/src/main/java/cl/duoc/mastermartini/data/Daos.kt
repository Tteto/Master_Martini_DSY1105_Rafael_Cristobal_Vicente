package cl.duoc.mastermartini.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun find(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun observe(email: String): Flow<User?>
}

@Dao
interface FavoriteDao {
    @Query("SELECT contentId FROM favorites WHERE email = :email")
    fun observe(email: String): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favorite: Favorite)

    @Delete
    suspend fun remove(favorite: Favorite)
}
