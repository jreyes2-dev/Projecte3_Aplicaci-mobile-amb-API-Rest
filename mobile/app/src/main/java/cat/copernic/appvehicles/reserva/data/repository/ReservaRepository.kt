package cat.copernic.appvehicles.reserva.data.repository

import cat.copernic.appvehicles.reserva.data.api.remote.ReservaApi
import cat.copernic.appvehicles.model.CancelReservaResponse
import cat.copernic.appvehicles.model.CreateReservaRequest
import cat.copernic.appvehicles.model.ReservaResponse
import retrofit2.HttpException

/**
 * Repositori encarregat de gestionar la comunicació amb l'API REST per a l'entitat Reserva.
 * Actua com a font de dades única (Single Source of Truth) per al ViewModel, abstraient
 * la complexitat de les crides de xarxa HTTP implementades amb Retrofit.
 *
 * @param api Interfície de Retrofit que defineix els endpoints del backend (Spring Boot).
 */
class ReservaRepository(private val api: ReservaApi) {

    /**
     * Envia una petició per crear una nova reserva.
     * Extreu el missatge d'error real del backend en cas de fallada HTTP (ex: 400 Bad Request),
     * evitant mostrar codis d'error genèrics a l'usuari.
     *
     * @param request Objecte de transferència de dades (DTO) amb les dades de la nova reserva.
     * @return La reserva creada i confirmada pel servidor.
     * @throws Exception Si la comunicació falla o el servidor retorna un error de negoci.
     */
    suspend fun crearReserva(request: CreateReservaRequest): ReservaResponse {
        return try {
            api.crearReserva(request)
        } catch (e: HttpException) {
            val msg = e.response()?.errorBody()?.string()
            throw Exception(msg ?: "S'ha produït un error al crear la reserva.")
        }
    }

    /**
     * Obté el detall complet d'una reserva a partir del seu identificador.
     *
     * @param id Identificador únic de la reserva.
     * @return Objecte [ReservaResponse] amb totes les dades.
     */
    suspend fun getReservaById(id: Long): ReservaResponse {
        return api.getReservaById(id)
    }

    /**
     * Sol·licita l'anul·lació d'una reserva existent.
     * Captura les excepcions HTTP (ex: 409 Conflict) per traslladar el motiu exacte del rebuig
     * generat per la lògica de negoci del backend (ex: "La reserva ja està iniciada").
     *
     * @param id Identificador de la reserva a cancel·lar.
     * @param userName Correu electrònic de l'usuari que executa l'acció (validació de rols).
     * @return DTO amb el resultat de la cancel·lació i l'import reemborsat.
     * @throws Exception Amb el missatge detallat del servidor si l'acció és denegada.
     */
    suspend fun cancelReserva(id: Long, userName: String): CancelReservaResponse {
        return try {
            api.cancelReserva(id, userName)
        } catch (e: HttpException) {
            val msg = e.response()?.errorBody()?.string()
            throw Exception(msg ?: "S'ha produït un error en anul·lar la reserva.")
        }
    }

    /**
     * Obté l'historial de reserves associat a un client específic.
     *
     * @param email Correu electrònic del client (identificador).
     * @param asc Booleà que indica la direcció de l'ordenació temporal (true = Ascendent).
     * @return Llista d'objectes [ReservaResponse] retinguts a la base de dades.
     */
    suspend fun getReservesClient(email: String, asc: Boolean): List<ReservaResponse> {
        val order = if (asc) "asc" else "desc"
        return api.getReservas(email = email, order = order)
    }
}