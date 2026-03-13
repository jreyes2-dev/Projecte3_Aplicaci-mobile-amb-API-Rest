package cat.copernic.appvehicles.model

/**
 * Data Transfer Object (DTO) que conté la confirmació del servidor en anul·lar una reserva.
 * Proveeix informació detallada sobre el resultat de l'operació financera derivada
 * de la cancel·lació, d'acord amb les polítiques de negoci (límit de dies per al reemborsament total).
 *
 * @property idReserva Identificador numèric de la reserva que s'ha processat.
 * @property refundAmount Quantitat monetària reemborsada al client. Pot ser nul·la (null) si l'anul·lació és tardana.
 * @property message Missatge informatiu emès pel backend (ex: "S'ha retornat la fiança").
 */
data class CancelReservaResponse(
    val idReserva: Long,
    val refundAmount: String?,
    val message: String
)