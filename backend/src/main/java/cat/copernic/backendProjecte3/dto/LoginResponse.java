package cat.copernic.backendProjecte3.dto;

/**
 *
 * @author bharr
 */

/**
 * DTO para la respuesta de un inicio de sesión exitoso.
 * No debe incluir NUNCA la contraseña.
 */
public class LoginResponse {

    private String email;
    private String nomComplet;
    // Podemos enviar un token simple (como un UUID generado) o simplemente con el email 
    // el móvil ya sabe que está logueado. Añadimos un campo token por si lo necesitas.
    private String token; 

    public LoginResponse() {
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}