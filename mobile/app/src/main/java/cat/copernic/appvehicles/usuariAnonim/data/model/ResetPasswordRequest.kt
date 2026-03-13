package cat.copernic.appvehicles.usuariAnonim.data.model

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)