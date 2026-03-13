package cat.copernic.appvehicles.model

/**
 * Model temporal (Mock)
 * Més endavant vindrà de la capa Domain
 */
data class VehicleMock(
    val id: String,
    val marca: String,
    val model: String,
    val variant: String,
    val preuHora: Double
)