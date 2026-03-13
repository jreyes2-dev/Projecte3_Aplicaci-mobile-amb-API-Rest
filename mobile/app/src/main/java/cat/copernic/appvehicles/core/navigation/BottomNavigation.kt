package cat.copernic.appvehicles.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import cat.copernic.appvehicles.R

@Composable
fun AppBottomNavigation(navController: NavHostController) {

    val items = listOf(
        Triple(AppRoutes.Vehicles.route, R.string.nav_home, Icons.Default.Home),
        Triple(AppRoutes.Reserves.route, R.string.nav_reservations, Icons.Default.ShoppingCart),
        Triple(AppRoutes.Perfil.route, R.string.nav_profile, Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        items.forEach { (route, labelRes, icon) ->

            val label = stringResource(labelRes)

            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    // Evitamos que haga cosas si ya estás pulsando la pestaña en la que estás
                    if (currentRoute != route) {
                        navController.navigate(route) {

                            // LIMPIEZA BRUSCA: Saca cualquier pantalla (Login/Register) que esté por encima de Inicio
                            popUpTo(navController.graph.findStartDestination().id) {
                                // Quitamos el saveState = true para que no se atasque
                                inclusive = false
                            }

                            // Evitamos abrir la misma pantalla varias veces
                            launchSingleTop = true

                            // Quitamos el restoreState = true para evitar bloqueos
                        }
                    }
                }
            )
        }
    }
}