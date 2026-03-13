package cat.copernic.appvehicles.model

data class VehicleResponse(
    val matricula: String,
    val marca: String,
    val model: String,
    val variant: String,
    val fotoUrl: String?,
    val potencia: String?,
    val color: String?,
    val limitQuilometratge: Int?,
    val preuHora: Double,
    val fiancaEstandard: Double?,
    val minDiesLloguer: Int?,
    val maxDiesLloguer: Int?,
    val fotoBase64: String? = null
)