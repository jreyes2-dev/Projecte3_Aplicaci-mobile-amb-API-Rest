package cat.copernic.appvehicles.model

data class Vehicle(
    val id: String,
    val marca: String,
    val model: String,
    val variant: String,
    val preuHora: Double,
    val fotoBase64: String? = null  // NUEVO
)