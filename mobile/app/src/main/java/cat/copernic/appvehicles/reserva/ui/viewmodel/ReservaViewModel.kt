package cat.copernic.appvehicles.reserva.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.model.CancelReservaResponse
import cat.copernic.appvehicles.model.CreateReservaRequest
import cat.copernic.appvehicles.model.ReservaResponse
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encarregat de gestionar la lògica de negoci i l'estat de les reserves.
 * Actua com a intermediari entre la capa de dades (Repository) i la capa de presentació (UI).
 * Utilitza StateFlows per exposar l'estat de manera reactiva i segura, garantint que la UI
 * s'actualitzi automàticament davant qualsevol canvi en les dades.
 *
 * @param repo Repositori de reserves que gestiona les crides de xarxa cap al backend.
 */
class ReservaViewModel(private val repo: ReservaRepository) : ViewModel() {

    // 1. Definició d'estats reactius (StateFlows)
    // S'utilitza el patró d'encapsulació: Mutable (privat) per modificar, StateFlow (públic) per llegir.

    private val _reserves = MutableStateFlow<List<ReservaResponse>>(emptyList())
    val reserves = _reserves.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _asc = MutableStateFlow(false)
    val asc = _asc.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    private val _creationResult = MutableStateFlow<Result<ReservaResponse>?>(null)
    val creationResult = _creationResult.asStateFlow()

    private val _reservaDetail = MutableStateFlow<ReservaResponse?>(null)
    val reservaDetail = _reservaDetail.asStateFlow()

    private val _cancelResult = MutableStateFlow<Result<CancelReservaResponse>?>(null)
    val cancelResult = _cancelResult.asStateFlow()

    // 2. Mètodes de neteja d'estats i memòria cau

    fun clearCreationResult() {
        _creationResult.value = null
    }

    fun clearCancelResult() {
        _cancelResult.value = null
    }

    /**
     * Neteja la llista de reserves en memòria.
     * Aquest mètode és cridat principalment quan l'usuari tanca la sessió (Logout)
     * per garantir la privacitat i seguretat de les dades.
     */
    fun clearReserves() {
        _reserves.value = emptyList()
        _errorMsg.value = null
    }

    // 3. Mètodes d'interacció amb el repositori (Backend)

    /**
     * Processa la sol·licitud de cancel·lació d'una reserva.
     * Actualitza l'estat local immediatament en cas d'èxit per oferir una resposta ràpida a la UI.
     *
     * @param id Identificador de la reserva a anul·lar.
     * @param userName Correu de l'usuari sol·licitant (per a validació de permisos).
     */
    fun cancelReserva(id: Long, userName: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repo.cancelReserva(id, userName)
                _cancelResult.value = Result.success(response)

                // Actualització optimista de l'estat local
                _reservaDetail.value = _reservaDetail.value?.copy(estat = "CANCELADA")
            } catch (e: Exception) {
                _cancelResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Obté els detalls complets d'una reserva específica.
     */
    fun loadReservaDetalle(id: Long) {
        viewModelScope.launch {
            try {
                _reservaDetail.value = repo.getReservaById(id)
            } catch (e: Exception) {
                _reservaDetail.value = null
            }
        }
    }

    /**
     * Inverteix l'ordre actual de la llista (Ascendent <-> Descendent) i recarrega les dades.
     */
    fun toggleOrder(email: String) {
        _asc.value = !_asc.value
        load(email)
    }

    /**
     * Descarrega l'historial de reserves associades a un client.
     *
     * @param email Correu electrònic del client actiu.
     */
    fun load(email: String) {
        viewModelScope.launch {
            _loading.value = true
            _errorMsg.value = null
            try {
                val llista = repo.getReservesClient(email, _asc.value)

                // Registre de depuració (Debug) per verificar la integritat de les dades rebudes des del backend.
                llista.forEach { reserva ->
                    Log.d(
                        "DEBUG_RESERVA",
                        "Reserva ID: ${reserva.idReserva} | Coche: ${reserva.vehicleMatricula} | FotoBase64: ${reserva.vehicleFotoBase64?.take(40)}"
                    )
                }

                _reserves.value = llista
            } catch (e: Exception) {
                _reserves.value = emptyList()
                _errorMsg.value = "S'ha produït un error de connexió: ${e.message}"
            }
            _loading.value = false
        }
    }

    /**
     * Envia una petició per crear una nova reserva a la base de dades.
     */
    fun crearReserva(request: CreateReservaRequest) {
        viewModelScope.launch {
            _loading.value = true
            _creationResult.value = try {
                val result = repo.crearReserva(request)
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
            _loading.value = false
        }
    }
}