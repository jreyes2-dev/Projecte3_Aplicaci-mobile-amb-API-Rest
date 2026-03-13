package cat.copernic.backendProjecte3.dto;

/**
 * DTO que representa una petición de restablecimiento de contraseña.
 *
 * <p>Contiene el token de recuperación generado previamente y la nueva
 * contraseña que el usuario desea establecer.</p>
 */
public class ResetPasswordRequest {

    /** Token temporal de recuperación de contraseña. */
    private String token;

    /** Nueva contraseña que se quiere guardar. */
    private String newPassword;

    /**
     * Constructor vacío necesario para la deserialización de peticiones JSON.
     */
    public ResetPasswordRequest() {
    }

    /**
     * Constructor con todos los datos necesarios para restablecer la contraseña.
     *
     * @param token token de recuperación.
     * @param newPassword nueva contraseña del usuario.
     */
    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    /**
     * Obtiene el token de recuperación.
     *
     * @return token recibido en la petición.
     */
    public String getToken() {
        return token;
    }

    /**
     * Establece el token de recuperación.
     *
     * @param token token temporal generado para el cambio de contraseña.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Obtiene la nueva contraseña.
     *
     * @return nueva contraseña introducida por el usuario.
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Establece la nueva contraseña.
     *
     * @param newPassword nueva contraseña a guardar.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}