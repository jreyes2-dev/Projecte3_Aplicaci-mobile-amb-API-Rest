package cat.copernic.appvehicles.model

/**
 * Data class que representa la respuesta exitosa del backend.
 * Contiene los datos del usuario logueado y el token de sesión.
 */
data class LoginResponse(
    val email: String,
    val nomComplet: String,
    val token: String
)
