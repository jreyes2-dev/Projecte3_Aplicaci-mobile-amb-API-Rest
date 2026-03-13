package cat.copernic.backendProjecte3.exceptions;

/**
 * Excepción lanzada cuando el token de recuperación de contraseña
 * no es válido, ha caducado o no cumple las condiciones esperadas.
 *
 * <p>Se utiliza durante el proceso de restablecimiento de contraseña
 * para indicar errores relacionados con la validez del token.</p>
 */
public class InvalidResetTokenException extends RuntimeException {

    /**
     * Crea una nueva excepción con el mensaje indicado.
     *
     * @param message descripción del error detectado.
     */
    public InvalidResetTokenException(String message) {
        super(message);
    }
}