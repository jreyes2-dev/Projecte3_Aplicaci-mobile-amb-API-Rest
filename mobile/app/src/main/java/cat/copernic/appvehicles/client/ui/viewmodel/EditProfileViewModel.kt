package cat.copernic.appvehicles.client.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.client.data.api.remote.ClientApiService
import cat.copernic.appvehicles.client.data.model.ClientUpdateRequest
import cat.copernic.appvehicles.client.data.repository.ClientRepository
import cat.copernic.appvehicles.core.auth.SessionManager
import cat.copernic.appvehicles.core.network.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val dni: String? = null,
    val nomComplet: String = "",
    val email: String = "",
    val telefon: String = "",
    val adreca: String = "",
    val nacionalitat: String = "",
    val numeroTargetaCredit: String = "",
    val dataCaducitatDni: String = "",
    val tipusCarnetConduir: String = "",
    val dataCaducitatCarnet: String = "",
    val photoUri: String? = null,
    val dniImageUri: String? = null,
    val licenseImageUri: String? = null,
    val messageKey: String? = null,
    val errorKeys: List<String> = emptyList() // AHORA ES UNA LISTA
)

class EditProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val sessionStore = SessionManager(app.applicationContext)
    private val api: ClientApiService = RetrofitProvider.retrofitApi.create(ClientApiService::class.java)
    private val repo = ClientRepository(api)

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorKeys = emptyList(), messageKey = null)

            val sessionEmail = sessionStore.userEmailFlow.first()
            if (sessionEmail.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorKeys = listOf("session_missing_email"))
                return@launch
            }

            val response = repo.getClient(sessionEmail)
            if (response.isSuccessful && response.body() != null) {
                val p = response.body()!!

                android.util.Log.d("DEBUG_PERFIL", "⬇️ DESCARGANDO: Foto de perfil recibida: ${p.fotoPerfil?.take(40)}")
                // AHORA EL BACKEND NOS ENVÍA BASE64 DIRECTAMENTE
                // Para que la UI (Coil) lo pinte como si fuera una URI, le añadimos este prefijo estándar:
                val prefix = "data:image/jpeg;base64,"

                val base64Dni = p.imatgeDni?.let { if (it.isNotBlank()) prefix + it else null }
                val base64Carnet = p.imatgeCarnet?.let { if (it.isNotBlank()) prefix + it else null }
                val base64Perfil = p.fotoPerfil?.let { if (it.isNotBlank()) prefix + it else null }

                _uiState.value = EditProfileUiState(
                    isLoading = false,
                    dni = p.dni,
                    nomComplet = p.nomComplet,
                    email = p.email,
                    telefon = p.telefon.orEmpty(),
                    adreca = p.adreca.orEmpty(),
                    nacionalitat = p.nacionalitat.orEmpty(),
                    numeroTargetaCredit = p.numeroTargetaCredit.orEmpty(),
                    dataCaducitatDni = p.dataCaducitatDni.orEmpty(),
                    tipusCarnetConduir = p.tipusCarnetConduir.orEmpty(),
                    dataCaducitatCarnet = p.dataCaducitatCarnet.orEmpty(),

                    photoUri = base64Perfil,
                    // Asignamos el Base64 listo para pintar
                    dniImageUri = base64Dni,
                    licenseImageUri = base64Carnet
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, errorKeys = listOf("profile_load_error"))
            }
        }
    }

    fun onFieldChange(
        nomComplet: String? = null, telefon: String? = null, adreca: String? = null,
        nacionalitat: String? = null, numeroTargetaCredit: String? = null,
        dataCaducitatDni: String? = null, tipusCarnetConduir: String? = null,
        dataCaducitatCarnet: String? = null
    ) {
        val s = _uiState.value
        _uiState.value = s.copy(
            nomComplet = nomComplet ?: s.nomComplet,
            telefon = telefon ?: s.telefon,
            adreca = adreca ?: s.adreca,
            nacionalitat = nacionalitat ?: s.nacionalitat,
            numeroTargetaCredit = numeroTargetaCredit ?: s.numeroTargetaCredit,
            dataCaducitatDni = dataCaducitatDni ?: s.dataCaducitatDni,
            tipusCarnetConduir = tipusCarnetConduir ?: s.tipusCarnetConduir,
            dataCaducitatCarnet = dataCaducitatCarnet ?: s.dataCaducitatCarnet,
            messageKey = null,
            errorKeys = emptyList() // Limpiamos errores al escribir
        )
    }

    fun onPhotoPicked(uri: String?) { _uiState.value = _uiState.value.copy(photoUri = uri, messageKey = null, errorKeys = emptyList()) }
    fun onDniImagePicked(uri: String?) { _uiState.value = _uiState.value.copy(dniImageUri = uri, messageKey = null, errorKeys = emptyList()) }
    fun onLicenseImagePicked(uri: String?) { _uiState.value = _uiState.value.copy(licenseImageUri = uri, messageKey = null, errorKeys = emptyList()) }

    private fun uriToBase64(context: android.content.Context, uriString: String?): String? {
        if (uriString.isNullOrBlank()) return null

        // Si el string ya empieza por "data:image", significa que es la foto antigua del servidor.
        // No hace falta volver a enviarla. Devolvemos null para que el backend no la sobrescriba.
        if (uriString.startsWith("data:image")) return null

        return try {
            val uri = android.net.Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP) else null
        } catch (e: Exception) {
            null
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            val s = _uiState.value
            val email = s.email
            val errors = mutableListOf<String>() // ACUMULADOR DE ERRORES

            if (email.isBlank()) {
                _uiState.value = s.copy(errorKeys = listOf("session_missing_email"))
                return@launch
            }

            // --- VALIDACIONES ACUMULATIVAS ---
            val regexNom = "^[a-zA-ZÀ-ÿ\\s]+$".toRegex()
            if (s.nomComplet.isBlank()) errors.add("full_name_required")
            else if (!s.nomComplet.matches(regexNom)) errors.add("invalid_name_format")

            val regexData = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

            if (s.dataCaducitatCarnet.isNotBlank()) {
                if (!s.dataCaducitatCarnet.matches(regexData)) {
                    errors.add("invalid_date_format")
                } else {
                    try {
                        val dataParsed = java.time.LocalDate.parse(s.dataCaducitatCarnet)
                        if (dataParsed.isBefore(java.time.LocalDate.now())) errors.add("license_expired")
                    } catch (e: Exception) { errors.add("invalid_date") }
                }
            }

            if (s.dataCaducitatDni.isNotBlank()) {
                if (!s.dataCaducitatDni.matches(regexData)) {
                    errors.add("invalid_date_format_dni")
                } else {
                    try {
                        val dataParsed = java.time.LocalDate.parse(s.dataCaducitatDni)
                        if (dataParsed.isBefore(java.time.LocalDate.now())) errors.add("dni_expired")
                    } catch (e: Exception) { errors.add("invalid_date_dni") }
                }
            }

            val regexTargeta = "^[0-9]{13,19}$".toRegex()
            if (s.numeroTargetaCredit.isNotBlank() && !s.numeroTargetaCredit.matches(regexTargeta)) {
                errors.add("invalid_card_format")
            }

            // SI HAY ERRORES, LOS MOSTRAMOS TODOS Y PARAMOS
            if (errors.isNotEmpty()) {
                _uiState.value = s.copy(errorKeys = errors)
                return@launch
            }

            _uiState.value = s.copy(isLoading = true, errorKeys = emptyList(), messageKey = null)

            val req = ClientUpdateRequest(
                nomComplet = s.nomComplet,
                telefon = s.telefon.ifBlank { null },
                adreca = s.adreca.ifBlank { null },
                nacionalitat = s.nacionalitat.ifBlank { null },
                numeroTargetaCredit = s.numeroTargetaCredit.ifBlank { null },
                dataCaducitatDni = s.dataCaducitatDni.ifBlank { null },
                tipusCarnetConduir = s.tipusCarnetConduir.ifBlank { null },
                dataCaducitatCarnet = s.dataCaducitatCarnet.ifBlank { null },

                        // ¡Ahora sí, 100% Kotlin!
                imatgeDni = uriToBase64(getApplication<Application>(), s.dniImageUri),
                imatgeCarnet = uriToBase64(getApplication<Application>(), s.licenseImageUri),
                fotoPerfil = uriToBase64(getApplication<Application>(), s.photoUri)
            )
            android.util.Log.d("DEBUG_PERFIL", "⬆️ SUBIENDO: Foto de perfil a enviar: ${req.fotoPerfil?.take(40)}")

            val response = repo.updateClient(email, req)
            if (response.isSuccessful) {
                _uiState.value = _uiState.value.copy(isLoading = false, messageKey = "profile_saved")
            } else {
                android.util.Log.e("API_ERROR_PERFIL", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                _uiState.value = _uiState.value.copy(isLoading = false, errorKeys = listOf("profile_save_error"))
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            sessionStore.clearSession()
            onSuccess()
        }
    }
}