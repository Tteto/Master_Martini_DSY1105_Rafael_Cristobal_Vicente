package cl.duoc.mastermartini.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mastermartini.data.AppRepository
import cl.duoc.mastermartini.data.Content
import cl.duoc.mastermartini.data.ContentData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContentViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AppRepository(app)
    private val email = MutableStateFlow<String?>(null)

    val category = MutableStateFlow("Todos")
    val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val favorites: StateFlow<Set<Int>> = email
        .flatMapLatest { e -> if (e == null) flowOf(emptyList()) else repo.observeFavorites(e) }
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val items: StateFlow<List<Content>> = combine(category, query) { cat, q ->
        ContentData.items.filter { c ->
            (cat == "Todos" || c.type == cat) &&
                (q.isBlank() || c.title.contains(q.trim(), ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ContentData.items)

    fun setUser(e: String?) { email.value = e }

    fun find(id: Int): Content? = ContentData.items.firstOrNull { it.id == id }

    fun toggleFavorite(id: Int) {
        val e = email.value ?: return
        viewModelScope.launch {
            if (id in favorites.value) repo.removeFavorite(e, id) else repo.addFavorite(e, id)
        }
    }
}
