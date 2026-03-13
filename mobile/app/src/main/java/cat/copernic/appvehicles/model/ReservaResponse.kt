package cat.copernic.appvehicles.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) que representa la resposta del servidor en consultar una reserva.
 * Utilitza la llibreria Gson per mapejar automàticament el JSON retornat per Spring Boot
 * als atributs natius de Kotlin mitjançant l'anotació [@SerializedName].
 */
data class ReservaResponse(
    @SerializedName("idReserva")
    val idReserva: Long,

    @SerializedName("dataInici")
    val dataInici: String,

    @SerializedName("dataFi")
    val dataFi: String,

    @SerializedName("clientEmail")
    val clientEmail: String,

    @SerializedName("vehicleMatricula")
    val vehicleMatricula: String,

    @SerializedName("importTotal")
    val importTotal: String,

    @SerializedName("fiancaPagada")
    val fiancaPagada: String,

    /**
     * Estat actual de la reserva (Ex: "ACTIVA", "CANCELADA", "FINALITZADA").
     * S'inicialitza com a "ACTIVA" per defecte per garantir la retrocompatibilitat
     * si el payload JSON omet aquest camp.
     */
    @SerializedName("estat")
    val estat: String? = "ACTIVA",

    /**
     * Cadena de text que conté la imatge del vehicle codificada en format Base64.
     * Aquesta dada s'injecta directament des de la base de dades per estalviar crides HTTP
     * addicionals a l'hora de renderitzar les miniatures.
     */
    @SerializedName("vehicleFotoBase64")
    val vehicleFotoBase64: String? = null
)