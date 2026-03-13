package cat.copernic.appvehicles.client.data.model

data class ClientProfileDto(
    val dni: String,
    val nomComplet: String,
    val email: String,
    val telefon: String?,
    val adreca: String?,
    val nacionalitat: String?,
    val numeroTargetaCredit: String?,
    val dataCaducitatDni: String?,
    val tipusCarnetConduir: String?,
    val dataCaducitatCarnet: String?,

    val imatgeDni: String? = null,
    val imatgeCarnet: String? = null,
    val fotoPerfil: String? = null
)