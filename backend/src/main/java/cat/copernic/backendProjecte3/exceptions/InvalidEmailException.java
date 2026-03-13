package cat.copernic.backendProjecte3.exceptions;

/**
 * Excepción lanzada cuando un correo electrónico es nulo, está vacío
 * o no cumple el formato esperado.
 *
 * <p>Se utiliza principalmente en los procesos donde es necesario
 * validar el email antes de continuar con la lógica de negocio.</p>
 */
public class InvalidEmailException extends RuntimeException {

    /**
     * Crea una nueva excepción con el mensaje indicado.
     *
     * @param message descripción del problema detectado en el email.
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}