package cat.copernic.appvehicles.usuariAnonim.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.model.ClientRegisterRequest
import cat.copernic.appvehicles.usuariAnonim.data.repository.AuthRepository
import cat.copernic.appvehicles.usuariAnonim.ui.view.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri
import android.util.Base64
import java.io.InputStream
import cat.copernic.appvehicles.core.composables.uriToFile
import com.google.gson.Gson

/**
 * ViewModel encarregat de gestionar la lògica de negoci de la pantalla de registre.
 * * @property repository El repositori d'autenticació per realitzar les crides a l'API.
 */
class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Actualitza l'estat global de la interfície d'usuari.
     *
     * @param newState Nou estat que substituirà l'actual.
     */
    fun updateState(newState: RegisterUiState) {
        _uiState.update { newState }
    }

    /**
     * Gestiona el procés de registre d'un nou client, incloent validacions i conversió d'imatges.
     *
     * @param context El context de l'aplicació per accedir al contentResolver.
     */
    fun register(context: Context) {
        val currentState = _uiState.value

        val regexData = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

        if (!currentState.dataCaducitatLlicencia.matches(regexData)) {
            _uiState.update { it.copy(errorMessage = "Format de data de llicència incorrecte (YYYY-MM-DD).") }
            return
        }

        try {
            val dataParsed = java.time.LocalDate.parse(currentState.dataCaducitatLlicencia)
            if (dataParsed.isBefore(java.time.LocalDate.now())) {
                _uiState.update { it.copy(errorMessage = "La llicència de conduir no pot estar caducada.") }
                return
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Data de llicència invàlida.") }
            return
        }

        if (currentState.nomComplet.isBlank() || currentState.email.isBlank() || currentState.password.isBlank() || currentState.numeroIdentificacio.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Falten camps obligatoris") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val imatgeDniBase64 = uriToBase64(context, currentState.fotoIdentificacioUri)
                val imatgeCarnetBase64 = uriToBase64(context, currentState.fotoLlicenciaUri)
                val imatgePerfilBase64 = uriToBase64(context, currentState.fotoPerfilUri)

                if (imatgeDniBase64.isBlank() || imatgeCarnetBase64.isBlank()) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error en llegir les imatges. Torna a seleccionar-les.")
                    }
                    return@launch
                }

                val request = ClientRegisterRequest(
                    email = currentState.email,
                    password = currentState.password,
                    nomComplet = currentState.nomComplet,
                    dni = currentState.numeroIdentificacio,
                    dataCaducitatDni = currentState.dataCaducitatId.ifBlank { "2025-01-01" },
                    imatgeDni = imatgeDniBase64,
                    nacionalitat = currentState.nacionalitat,
                    fotoPerfil = imatgePerfilBase64,
                    adreca = currentState.adreca,
                    tipusCarnetConduir = currentState.tipusLlicencia,
                    dataCaducitatCarnet = currentState.dataCaducitatLlicencia.ifBlank { "2030-01-01" },
                    imatgeCarnet = imatgeCarnetBase64,
                    numeroTargetaCredit = currentState.numeroTargetaCredit
                )

                val result = repository.register(request)

                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error desconegut"

                    if (errorMsg.contains("409")) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Aquest email o DNI ja estan registrats.")
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error de connexió: ${e.message}")
                }
            }
        }
    }
}

/**
 * Converteix una URI d'imatge en una cadena codificada en Base64.
 *
 * @param context El context necessari per obrir l'stream de dades.
 * @param uriString La URI en format String.
 * @return La cadena en Base64 o una cadena buida en cas d'error.
 */
private fun uriToBase64(context: Context, uriString: String?): String {
    if (uriString.isNullOrBlank()) return ""
    return try {
        val uri = Uri.parse(uriString)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()

        if (bytes != null) {
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

/**
 * Factoria per instanciar el RegisterViewModel amb el seu repositori corresponent.
 *
 * @property repository El repositori que s'injectarà al ViewModel.
 */
class RegisterViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    /**
     * Crea una nova instància del ViewModel sol·licitat.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}