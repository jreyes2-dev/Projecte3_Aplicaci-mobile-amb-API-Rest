package cat.copernic.appvehicles.vehicle.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    val matricula: String,
    val marca: String,
    val model: String,
    val variant: String,
    val preuHora: Double,
    val fotoBase64: String? = null,  // NUEVO
    val fotoUrl: String? = null,
    val potencia: String? = null,
    val color: String? = null,
    val limitQuilometratge: Int? = null,
    val fiancaEstandard: Double? = null,
    val minDiesLloguer: Int? = null,
    val maxDiesLloguer: Int? = null,
    val tipusVehicle: String? = null,
    val estatVehicle: String? = null
)