package cat.copernic.appvehicles.model

/**
 * Data class que representa el cuerpo JSON que se enviará al Backend.
 * Debe coincidir campo por campo con ClientRegistreDTO.java
 */
data class ClientRegisterRequest(
    val email: String,
    val password: String,
    val nomComplet: String,

    val dni: String,
    val dataCaducitatDni: String, // Formato "yyyy-MM-dd"
    val imatgeDni: String,
    val nacionalitat: String,
    val adreca: String,
    val fotoPerfil: String? = null,

    // Datos de Conducción
    val tipusCarnetConduir: String,
    val dataCaducitatCarnet: String, // Formato "yyyy-MM-dd"
    val imatgeCarnet: String,


    val numeroTargetaCredit: String
)
