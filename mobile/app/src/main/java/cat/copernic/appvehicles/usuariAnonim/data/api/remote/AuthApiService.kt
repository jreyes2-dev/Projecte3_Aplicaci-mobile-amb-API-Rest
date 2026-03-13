package cat.copernic.appvehicles.usuariAnonim.data.api.remote

import cat.copernic.appvehicles.model.ClientRegisterRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryRequest
import cat.copernic.appvehicles.usuariAnonim.data.model.PasswordRecoveryResponse
import cat.copernic.appvehicles.usuariAnonim.data.model.ResetPasswordRequest
import cat.copernic.appvehicles.model.LoginRequest
import cat.copernic.appvehicles.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * @param request Objeto con los datos necesarios para el registro.
     * @return Respuesta de la operación sin cuerpo de datos.
     */
    @POST("auth/register")
    suspend fun register(@Body request: ClientRegisterRequest): Response<Unit>

    /**
     * Inicia el proceso de recuperación de contraseña.
     *
     * @param request Objeto que contiene la información para identificar al usuario.
     * @return Respuesta con el estado del proceso de recuperación.
     */
    @POST("auth/recover-password")
    suspend fun recoverPassword(
        @Body request: PasswordRecoveryRequest
    ): Response<PasswordRecoveryResponse>

    /**
     * Establece una nueva contraseña tras el proceso de recuperación.
     *
     * @param request Objeto con el token y la nueva contraseña.
     * @return Respuesta confirmando el cambio de contraseña.
     */
    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<PasswordRecoveryResponse>

    /**
     * Autentica a un usuario en el sistema.
     *
     * @param request Credenciales de acceso del usuario.
     * @return Respuesta con los datos de sesión y el token de acceso.
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}