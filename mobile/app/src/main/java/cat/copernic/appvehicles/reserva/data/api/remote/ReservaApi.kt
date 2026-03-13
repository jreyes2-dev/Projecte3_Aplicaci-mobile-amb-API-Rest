package cat.copernic.appvehicles.reserva.data.api.remote

import cat.copernic.appvehicles.model.CancelReservaResponse // <-- Ojo, si los mueves, el import cambiará a esto
import cat.copernic.appvehicles.model.CreateReservaRequest
import cat.copernic.appvehicles.model.ReservaResponse
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfície de Retrofit que defineix els punts d'accés (endpoints) de l'API REST
 * del servidor Spring Boot per a la gestió de les reserves.
 * Utilitza funcions suspeses (suspend) per permetre l'execució asíncrona mitjançant Corrutines.
 */
interface ReservaApi {

    /**
     * Sol·licita l'anul·lació d'una reserva al servidor.
     * * @param id L'identificador de la reserva a la URL de la petició.
     * @param userName El correu de l'usuari que executa l'acció (passat com a paràmetre de consulta '?userName=').
     * @return [CancelReservaResponse] amb el resultat de l'operació (ex: import retornat).
     */
    @DELETE("api/reserves/{id}")
    suspend fun cancelReserva(
        @Path("id") id: Long,
        @Query("userName") userName: String
    ): CancelReservaResponse

    /**
     * Obté els detalls complets d'una reserva específica.
     * * @param id L'identificador únic de la reserva.
     * @return [ReservaResponse] amb tota la informació de la reserva seleccionada.
     */
    @GET("api/reserves/{id}")
    suspend fun getReservaById(
        @Path("id") id: Long
    ): ReservaResponse

    /**
     * Crea una nova reserva enviant les dades necessàries en el cos (body) de la petició.
     * * @param request Objecte [CreateReservaRequest] amb la matrícula, dates i client.
     * @return [ReservaResponse] amb la reserva ja persistida i confirmada pel backend.
     */
    @POST("api/reserves")
    suspend fun crearReserva(
        @Body request: CreateReservaRequest
    ): ReservaResponse

    /**
     * Obté la llista de reserves associades a un client concret.
     * * @param email El correu electrònic del client per filtrar la cerca.
     * @param order L'ordre d'ordenació cronològica (asc/desc). Per defecte és "desc" (més recents primer).
     * @return Una llista d'objectes [ReservaResponse].
     */
    @GET("api/reserves")
    suspend fun getReservas(
        @Query("email") email: String,
        @Query("order") order: String = "desc"
    ): List<ReservaResponse>
}