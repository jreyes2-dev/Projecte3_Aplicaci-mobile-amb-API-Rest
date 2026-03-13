package cat.copernic.backendProjecte3.dto;

/**
 * DTO de respuesta para las operaciones relacionadas con recuperación
 * y restablecimiento de contraseña.
 *
 * <p>Devuelve un código identificativo del resultado y un mensaje
 * descriptivo para el cliente.</p>
 */
public class PasswordRecoveryResponse {

    /** Código funcional del resultado de la operación. */
    private String code;

    /** Mensaje descriptivo asociado al resultado. */
    private String message;

    /**
     * Constructor vacío necesario para la serialización y deserialización.
     */
    public PasswordRecoveryResponse() {
    }

    /**
     * Constructor completo de la respuesta.
     *
     * @param code código del resultado.
     * @param message mensaje descriptivo.
     */
    public PasswordRecoveryResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Obtiene el código del resultado.
     *
     * @return código de respuesta.
     */
    public String getCode() {
        return code;
    }

    /**
     * Establece el código del resultado.
     *
     * @param code código de respuesta.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Obtiene el mensaje descriptivo de la respuesta.
     *
     * @return mensaje informativo.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el mensaje descriptivo de la respuesta.
     *
     * @param message mensaje informativo.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}