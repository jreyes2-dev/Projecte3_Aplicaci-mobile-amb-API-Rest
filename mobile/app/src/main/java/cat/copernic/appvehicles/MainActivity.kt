package cat.copernic.appvehicles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cat.copernic.appvehicles.core.auth.SessionManager
import cat.copernic.appvehicles.core.navigation.MainScreen
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
// Importem el proveïdor centralitzat!
import cat.copernic.appvehicles.core.network.RetrofitProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Utilitzem l'API de Login centralitzada (adéu Retrofit local!)
        val authService = RetrofitProvider.authApi

        // 2. Inicialitzem el gestor de sessions i el repositori
        val sessionManager = SessionManager(applicationContext)
        val authRepository = AuthRepository(authService, sessionManager)

        // 3. Arrenquem la interfície gràfica
        setContent {
            AppVehiclesTheme {
                MainScreen(authRepository)
            }
        }
    }
}