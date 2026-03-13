package cat.copernic.appvehicles.client.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.client.ui.viewmodel.LoginViewModel
import cat.copernic.appvehicles.core.composables.ReusableTextField

/**
 * Pantalla principal d'autenticació (Login) per als clients.
 *
 * @param vm El ViewModel que gestiona l'estat i la lògica del login.
 * @param onLoginSuccess Callback que s'executa quan l'usuari s'ha autenticat correctament.
 * @param onNavigateToRecover Callback per navegar a la pantalla de recuperació de contrasenya.
 * @param onNavigateToRegister Callback per navegar a la pantalla de registre de nou usuari.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: LoginViewModel,
    onLoginSuccess: () -> Unit = {},
    onNavigateToRecover: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val state by vm.uiState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.login_title)) }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = stringResource(R.string.login_subtitle),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ReusableTextField(
                        value = state.email,
                        onValueChange = vm::onEmailChanged,
                        label = stringResource(R.string.email_usuari),
                        placeholder = stringResource(R.string.email_example_com)
                    )

                    state.emailError?.let { errorKey ->
                        Text(
                            text = stringResource(errorKeyToRes(errorKey)),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.password_label)) },
                        singleLine = true,
                        isError = state.passwordError != null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    stringResource(
                                        if (passwordVisible) R.string.hide else R.string.show
                                    )
                                )
                            }
                        }
                    )

                    state.passwordError?.let {
                        Text(
                            text = stringResource(errorKeyToRes(it)),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    state.generalError?.let {
                        val resId = try { errorKeyToRes(it) } catch (e: Exception) { null }
                        Text(
                            text = if (resId != null && resId != R.string.error_generic) stringResource(resId) else it,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onNavigateToRecover) {
                            Text(stringResource(R.string.forgot_password))
                        }
                        TextButton(onClick = onNavigateToRegister) {
                            Text(stringResource(R.string.go_to_register))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val canLogin = state.email.isNotBlank() && state.password.isNotBlank() && !state.isLoading

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            vm.onLoginClick()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canLogin
                    ) {
                        Text(stringResource(R.string.login_action))
                    }

                    if (state.isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Tradueix una clau d'error provinent del sistema o del backend al seu recurs de cadena corresponent.
 *
 * @param key La clau identificadora de l'error.
 * @return L'identificador del recurs string (R.string).
 */
private fun errorKeyToRes(key: String): Int {
    val lowercaseKey = key.lowercase()

    return when {
        lowercaseKey == "email_required" -> R.string.error_email_required
        lowercaseKey == "email_invalid" -> R.string.error_email_invalid
        lowercaseKey == "password_required" -> R.string.error_password_required
        lowercaseKey == "invalid_credentials" -> R.string.error_invalid_credentials
        lowercaseKey == "user_not_exist" -> R.string.error_user_not_exist

        lowercaseKey.contains("usuari no existeix") || lowercaseKey.contains("usuari no trobat") -> R.string.error_user_not_exist

        lowercaseKey.contains("credencials incorrectes") ||
                lowercaseKey.contains("bad credentials") ||
                lowercaseKey.contains("bad password") ||
                lowercaseKey.contains("wrong password") ||
                lowercaseKey.contains("contrasenya incorrecta") ||
                lowercaseKey.contains("contraseña incorrecta") -> R.string.error_invalid_credentials

        else -> R.string.error_generic
    }
}