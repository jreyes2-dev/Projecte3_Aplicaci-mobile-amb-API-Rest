package cat.copernic.appvehicles.model

/**
 * Data Transfer Object (DTO) utilitzat per encapsular les dades necessàries
 * a l'hora d'enviar el cos (body) d'una petició POST per crear una nova reserva.
 *
 * @property emailClient Correu del client que gaudirà de la reserva.
 * @property matricula Identificador únic del vehicle seleccionat.
 * @property dataInici Data d'inici del lloguer en format "YYYY-MM-DD".
 * @property dataFi Data de finalització del lloguer en format "YYYY-MM-DD".
 * @property userName Correu de l'usuari actiu (utilitzat pel backend per verificar l'autoria de la petició).
 */
data class CreateReservaRequest(
    val emailClient: String,
    val matricula: String,
    val dataInici: String,
    val dataFi: String,
    val userName: String
)