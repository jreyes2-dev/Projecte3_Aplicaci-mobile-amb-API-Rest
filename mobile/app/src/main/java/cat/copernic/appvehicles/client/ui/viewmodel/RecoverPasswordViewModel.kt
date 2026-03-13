package cat.copernic.appvehicles.client.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.core.network.RetrofitProvider
import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de recuperación y restablecimiento de contraseña.
 *
 * @property email correo introducido para solicitar la recuperación.
 * @property token token recibido por correo.
 * @property newPassword nueva contraseña.
 * @property confirmPassword confirmación de la nueva contraseña.
 * @property isLoading indica si hay una operación en curso.
 * @property errorKey clave de error para traducir desde recursos.
 * @property successKey clave de éxito para traducir desde recursos.
 */
data class RecoverPasswordUiState(
    val email: String = "",
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorKey: String? = null,
    val successKey: String? = null
)

/**
 * ViewModel de la funcionalidad RF03 de recuperación de contraseña.
 *
 * <p>Gestiona el estado de la IU usando {@code MutableStateFlow} y ejecuta las
 * llamadas asíncronas al backend mediante corrutinas.
 */
class RecoverPasswordViewModel : ViewModel() {

    private val repository = AuthRepository(
        api = RetrofitProvider.retrofit.create(AuthApiService::class.java)
    )

    private val _uiState = MutableStateFlow(RecoverPasswordUiState())
    val uiState: StateFlow<RecoverPasswordUiState> = _uiState

    /**
     * Actualiza el email del formulario.
     *
     * @param value nuevo valor introducido por el usuario.
     */
    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorKey = null, successKey = null) }
    }

    /**
     * Actualiza el token del formulario de restablecimiento.
     *
     * @param value token introducido por el usuario.
     */
    fun onTokenChanged(value: String) {
        _uiState.update { it.copy(token = value, errorKey = null, successKey = null) }
    }

    /**
     * Actualiza la nueva contraseña.
     *
     * @param value nueva contraseña.
     */
    fun onNewPasswordChanged(value: String) {
        _uiState.update { it.copy(newPassword = value, errorKey = null, successKey = null) }
    }

    /**
     * Actualiza la confirmación de contraseña.
     *
     * @param value confirmación escrita por el usuario.
     */
    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorKey = null, successKey = null) }
    }

    /**
     * Solicita al backend el envío del correo de recuperación.
     *
     * <p>Valida previamente que el email no esté vacío y tenga un formato correcto.
     */
    fun sendRecoveryEmail() {
        val email = _uiState.value.email.trim()

        if (email.isBlank()) {
            _uiState.update { it.copy(errorKey = "email_required", successKey = null) }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorKey = "email_invalid", successKey = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKey = null, successKey = null) }

            repository.recoverPassword(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successKey = "recover_sent",
                            errorKey = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorKey = "recover_failed",
                            successKey = null
                        )
                    }
                }
        }
    }

    /**
     * Envía al backend el token de recuperación junto con la nueva contraseña.
     *
     * <p>Valida token, longitud mínima y coincidencia entre contraseña y confirmación
     * antes de hacer la petición remota.
     */
    fun resetPassword() {
        val token = _uiState.value.token.trim()
        val newPassword = _uiState.value.newPassword
        val confirmPassword = _uiState.value.confirmPassword

        if (token.isBlank()) {
            _uiState.update { it.copy(errorKey = "token_required", successKey = null) }
            return
        }

        if (newPassword.isBlank()) {
            _uiState.update { it.copy(errorKey = "password_required", successKey = null) }
            return
        }

        if (newPassword.length < 6) {
            _uiState.update { it.copy(errorKey = "password_too_short", successKey = null) }
            return
        }

        if (newPassword != confirmPassword) {
            _uiState.update { it.copy(errorKey = "password_mismatch", successKey = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorKey = null, successKey = null) }

            repository.resetPassword(token, newPassword)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successKey = "password_reset_ok",
                            errorKey = null,
                            token = "",
                            newPassword = "",
                            confirmPassword = ""
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorKey = "reset_failed",
                            successKey = null
                        )
                    }
                }
        }
    }
}