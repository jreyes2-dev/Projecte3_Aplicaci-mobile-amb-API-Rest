package cat.copernic.appvehicles.model


/**
 * Data class que representa las credenciales que enviamos al backend.
 */
data class LoginRequest(
    val email: String,
    val password: String
)
