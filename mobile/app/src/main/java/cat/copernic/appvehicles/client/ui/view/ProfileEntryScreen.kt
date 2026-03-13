package cat.copernic.appvehicles.client.ui.view

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.core.auth.SessionManager
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterScreen
import cat.copernic.appvehicles.client.ui.viewmodel.LoginViewModel
import cat.copernic.appvehicles.client.ui.viewmodel.LoginViewModelFactory
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModelFactory
// Hemos quitado el import de 'first' porque usaremos collectAsState()

private enum class ProfileMode { LOGIN, RECOVER, REGISTER }

@Composable
fun ProfileEntryScreen(
    authRepository: AuthRepository,
    onLoginSuccessNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionStore = remember { SessionManager(context) }

    // Volvemos a tu estado manual predecible
    var isLoggedIn by remember { mutableStateOf(false) }
    var mode by rememberSaveable { mutableStateOf(ProfileMode.LOGIN) }

    // Leemos la sesión solo al entrar a la pestaña
    LaunchedEffect(Unit) {
        isLoggedIn = sessionStore.isLoggedIn.first()
    }

    if (isLoggedIn) {
        EditProfileScreen(
            onLoggedOut = {
                isLoggedIn = false // Apagamos la sesión visualmente
                mode = ProfileMode.LOGIN // Nos aseguramos de mostrar el Login
            }
        )
        return
    }

    // Un client només pot fer login quan la seva sessió està tancada o inexistent
    when (mode) {
        ProfileMode.LOGIN -> {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(authRepository)
            )

            LoginScreen(
                vm = loginViewModel,
                onLoginSuccess = {
                    onLoginSuccessNavigate()
                },
                onNavigateToRecover = { mode = ProfileMode.RECOVER },
                onNavigateToRegister = { mode = ProfileMode.REGISTER }
            )
        }

        ProfileMode.RECOVER -> RecoverPasswordScreen(
            onBackClick = { mode = ProfileMode.LOGIN }
        )

        ProfileMode.REGISTER -> {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(authRepository)
            )

            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = { mode = ProfileMode.LOGIN },
                onRegisterSuccess = {
                    mode = ProfileMode.LOGIN
                }
            )
        }
    }
}