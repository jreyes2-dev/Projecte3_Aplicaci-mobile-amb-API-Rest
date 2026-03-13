package cat.copernic.backendProjecte3.dto;

/**
 * DTO utilizado para solicitar la recuperación de contraseña.
 *
 * <p>Contiene únicamente el correo electrónico del usuario que desea
 * iniciar el proceso de recuperación.</p>
 */
public class PasswordRecoveryRequest {

    /** Correo electrónico del usuario que solicita recuperar su contraseña. */
    private String email;

    /**
     * Constructor vacío necesario para la deserialización automática de JSON.
     */
    public PasswordRecoveryRequest() {
    }

    /**
     * Constructor con el correo del usuario.
     *
     * @param email correo electrónico asociado a la cuenta.
     */
    public PasswordRecoveryRequest(String email) {
        this.email = email;
    }

    /**
     * Obtiene el correo electrónico de la solicitud.
     *
     * @return email del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico de la solicitud.
     *
     * @param email email del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}