package cat.copernic.appvehicles.usuariAnonim.data.repository

import cat.copernic.appvehicles.core.auth.SessionManager
import cat.copernic.appvehicles.model.ClientRegisterRequest
import cat.copernic.appvehicles.model.LoginRequest
import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryResponse
import cat.copernic.appvehicles.usuariAnonim.data.model.ResetPasswordRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

/**
 * Repositorio de autenticación.
 *
 * <p>Se encarga de comunicar la aplicación móvil con los endpoints de autenticación
 * del backend. También persiste el estado de la sesión cuando el login se realiza
 * correctamente.
 *
 * @property api servicio Retrofit para autenticación.
 * @property sessionManager gestor opcional de sesión local. Es obligatorio para login,
 * pero no para registro o recuperación de contraseña.
 */
class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager? = null
) {

    /**
     * Registra un nuevo cliente enviando los datos del formulario y los ficheros
     * requeridos por multipart.
     *
     * @param clientData bloque JSON serializado con los datos del cliente.
     * @param fotoIdentificacio imagen del documento identificativo.
     * @param fotoLlicencia imagen de la licencia de conducir.
     * @return {@code Result.success(true)} si el alta se completa correctamente.
     */
    suspend fun register(request: ClientRegisterRequest): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Pasamos directamente el objeto request a la API
                val response = api.register(request)


                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        try {
                            JSONObject(errorBody).getString("error")
                        } catch (e: Exception) {
                            errorBody
                        }
                    } else {
                        "Error en el registre: Codi ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    /**
     * Solicita al backend el envío del correo de recuperación de contraseña.
     *
     * @param email correo electrónico del usuario.
     * @return respuesta normalizada del backend.
     */
    suspend fun recoverPassword(email: String): Result<PasswordRecoveryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.recoverPassword(PasswordRecoveryRequest(email))

                if (response.isSuccessful) {
                    Result.success(
                        response.body() ?: PasswordRecoveryResponse(
                            code = "recover_sent",
                            message = "If the email exists, you will receive recovery instructions shortly."
                        )
                    )
                } else {
                    Result.failure(
                        Exception(response.errorBody()?.string() ?: "Recovery failed")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Envía al backend el token de recuperación junto con la nueva contraseña.
     *
     * @param token token de recuperación recibido por email.
     * @param newPassword nueva contraseña elegida por el usuario.
     * @return respuesta normalizada del backend.
     */
    suspend fun resetPassword(
        token: String,
        newPassword: String
    ): Result<PasswordRecoveryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.resetPassword(ResetPasswordRequest(token, newPassword))

                if (response.isSuccessful) {
                    Result.success(
                        response.body() ?: PasswordRecoveryResponse(
                            code = "password_reset_ok",
                            message = "Your password has been updated successfully."
                        )
                    )
                } else {
                    Result.failure(
                        Exception(response.errorBody()?.string() ?: "Reset password failed")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Inicia sesión contra el backend y, si la autenticación es correcta, guarda
     * el estado de sesión en el dispositivo.
     *
     * <p>No se almacena nunca la contraseña localmente, cumpliendo el requisito
     * funcional de persistencia de sesión sin guardar credenciales sensibles.
     *
     * @param email email del usuario.
     * @param contrasenya contraseña en texto plano introducida por el usuario.
     * @return {@code Result.success(true)} si el login ha sido correcto.
     */
    suspend fun login(email: String, contrasenya: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email = email, password = contrasenya)
                val response = api.login(request)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        val localSessionManager = sessionManager
                            ?: return@withContext Result.failure(
                                Exception("SessionManager no disponible per guardar la sessió")
                            )

                        localSessionManager.saveSession(
                            email = body.email,
                            name = body.nomComplet,
                            token = body.token
                        )

                        Result.success(true)
                    } else {
                        Result.failure(Exception("Resposta buida del servidor"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        try {
                            JSONObject(errorBody).getString("error")
                        } catch (_: Exception) {
                            "Error en iniciar sessió"
                        }
                    } else {
                        "Error en iniciar sessió: Codi ${response.code()}"
                    }

                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error de connexió: ${e.message}"))
            }
        }
    }
}