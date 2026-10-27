package cl.duoc.mastermartini

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.mastermartini.ui.screens.DetailScreen
import cl.duoc.mastermartini.ui.screens.HomeScreen
import cl.duoc.mastermartini.ui.screens.LoginScreen
import cl.duoc.mastermartini.ui.screens.ProfileScreen
import cl.duoc.mastermartini.ui.screens.RegisterScreen
import cl.duoc.mastermartini.viewmodel.AuthViewModel
import cl.duoc.mastermartini.viewmodel.ContentViewModel

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Inicio", Icons.Filled.Home),
    Tab("favorites", "Favoritos", Icons.Filled.Favorite),
    Tab("profile", "Perfil", Icons.Filled.Person)
)

@Composable
fun AppNav() {
    val auth: AuthViewModel = viewModel()
    val content: ContentViewModel = viewModel()
    val nav = rememberNavController()
    val user by auth.user.collectAsState()
    val start = remember { if (auth.session.value != null) "home" else "login" }

    LaunchedEffect(user?.email) { content.setUser(user?.email) }

    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val showBar = route in tabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar {
                    tabs.forEach { t ->
                        NavigationBarItem(
                            selected = route == t.route,
                            onClick = {
                                nav.navigate(t.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(t.icon, contentDescription = t.label) },
                            label = { Text(t.label, fontSize = 14.sp) }
                        )
                    }
                }
            }
        }
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = start,
            modifier = Modifier.padding(pad),
            enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 10 } },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(200)) }
        ) {
            composable("login") {
                LoginScreen(
                    vm = auth,
                    onLogged = { nav.navigate("home") { popUpTo("login") { inclusive = true } } },
                    onRegister = { nav.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    vm = auth,
                    onRegistered = { nav.navigate("home") { popUpTo("login") { inclusive = true } } },
                    onBack = { nav.popBackStack() }
                )
            }
            composable("home") {
                HomeScreen(content, user?.name?.substringBefore(" ") ?: "", false) { nav.navigate("detail/$it") }
            }
            composable("favorites") {
                HomeScreen(content, user?.name?.substringBefore(" ") ?: "", true) { nav.navigate("detail/$it") }
            }
            composable("profile") {
                ProfileScreen(auth) {
                    nav.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            }
            composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) {
                DetailScreen(it.arguments?.getInt("id") ?: 0, content) { nav.popBackStack() }
            }
        }
    }
}
