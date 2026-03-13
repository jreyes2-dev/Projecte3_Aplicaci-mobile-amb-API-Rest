package cat.copernic.appvehicles.client.ui.viewmodel

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
) {
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() &&
                emailError == null && passwordError == null
}