package cat.copernic.backendProjecte3.dto;

/**
 *
 * @author bharr
 */

/**
 * DTO para la petición de inicio de sesión (Login).
 * Encapsula las credenciales enviadas desde la app móvil.
 */
public class LoginRequest {

    private String email;
    private String password;

    public LoginRequest() {
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}