package cat.copernic.appvehicles.client.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lògica d'autenticació per a la pantalla de Login.
 *
 * @property authRepository El repositori encarregat de les operacions d'autenticació.
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    /**
     * Actualitza l'email en l'estat de la interfície d'usuari.
     *
     * @param newValue El nou valor del correu electrònic.
     */
    fun onEmailChanged(newValue: String) {
        _uiState.update { it.copy(email = newValue) }
    }

    /**
     * Actualitza la contrasenya en l'estat i realitza una validació immediata.
     *
     * @param newValue La nova contrasenya introduïda.
     */
    fun onPasswordChanged(newValue: String) {
        _uiState.update { current ->
            current.copy(
                password = newValue,
                passwordError = validatePassword(newValue),
                generalError = null
            )
        }
    }

    /**
     * Executa el procés d'inici de sessió realitzant les validacions prèvies
     * i la crida al repositori de forma asíncrona.
     */
    fun onLoginClick() {
        _uiState.update { it.copy(emailError = null, passwordError = null, generalError = null) }

        val state = _uiState.value
        val llistaErrors = mutableListOf<String>()

        if (state.email.isBlank()) {
            llistaErrors.add("email_required")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            llistaErrors.add("email_invalid")
        }

        if (state.password.isBlank()) {
            llistaErrors.add("password_required")
        }

        if (llistaErrors.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    emailError = if (llistaErrors.contains("email_required") || llistaErrors.contains("email_invalid")) llistaErrors.first { it.startsWith("email") } else null,
                    passwordError = if (llistaErrors.contains("password_required")) "password_required" else null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            android.util.Log.d("DEBUG_LOGIN", "MÓVIL ENVÍA -> Email: [${state.email}] | Password: [${state.password}]")

            val result = authRepository.login(state.email, state.password)

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = true, generalError = null)
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, isLoggedIn = false, generalError = exception.message)
                    }
                }
            )
        }
    }

    /**
     * Valida si el format del correu electrònic és correcte.
     *
     * @param value El text a validar.
     * @return Una clau d'error o null si és vàlid.
     */
    private fun validateEmail(value: String): String? {
        if (value.isBlank()) return "email_required"
        if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) return "email_invalid"
        return null
    }

    /**
     * Valida si la contrasenya compleix els requisits mínims (no buida).
     *
     * @param value El text a validar.
     * @return Una clau d'error o null si és vàlid.
     */
    private fun validatePassword(value: String): String? {
        if (value.isBlank()) return "password_required"
        return null
    }
}