package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.business.ClientService;
import cat.copernic.backendProjecte3.business.UserLogic;
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import cat.copernic.backendProjecte3.dto.LoginRequest;
import cat.copernic.backendProjecte3.dto.LoginResponse;
import cat.copernic.backendProjecte3.dto.PasswordRecoveryRequest;
import cat.copernic.backendProjecte3.dto.PasswordRecoveryResponse;
import cat.copernic.backendProjecte3.dto.ResetPasswordRequest;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Usuari;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import cat.copernic.backendProjecte3.repository.UsuariRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

/**
 * Controlador REST de autenticación y registro.
 *
 * <p>Centraliza las operaciones relacionadas con:
 * <ul>
 *     <li>Registro de clientes.</li>
 *     <li>Inicio de sesión.</li>
 *     <li>Recuperación de contraseña.</li>
 *     <li>Restablecimiento de contraseña mediante token.</li>
 * </ul>
 *
 * <p>Este controlador da cobertura principalmente a los requisitos RF01, RF02 y RF03.
 *
 * <p>Los endpoints definidos en esta clase permiten tanto el acceso inicial de
 * usuarios al sistema como la gestión del ciclo de recuperación de credenciales
 * cuando un usuario no puede iniciar sesión con normalidad.</p>
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    /**
     * Servicio encargado de la lógica de negocio relacionada con clientes.
     *
     * <p>Se utiliza principalmente para registrar nuevos clientes.</p>
     */
    @Autowired
    private ClientService clientService;

    /**
     * Componente de lógica de negocio para autenticación y recuperación de contraseña.
     */
    @Autowired
    private UserLogic userLogic;

    /**
     * Repositorio de usuarios utilizado para recuperar la información persistida
     * necesaria durante el proceso de login.
     */
    @Autowired
    private UsuariRepository usuariRepository;

    /**
     * Utilidad para serialización y deserialización de objetos JSON.
     *
     * <p>Se mantiene inyectado en el controlador para posibles transformaciones
     * de datos relacionadas con peticiones o respuestas JSON.</p>
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Registra un nuevo cliente a partir de un bloque JSON y dos ficheros opcionales.
     *
     * <p>El campo {@code clientData} contiene el JSON serializado con los datos del
     * usuario. Las imágenes se reciben en multipart para permitir subir documentación
     * e identificación durante el proceso de registro.
     *
     * @param clientDataJson JSON con los datos del cliente.
     * @param fotoIdentificacio imagen del documento identificativo.
     * @param fotoLlicencia imagen de la licencia de conducir.
     * @return respuesta HTTP con el resultado del alta.
     */
    /**
     * Endpoint para registrar un cliente desde el móvil (recibe JSON con imágenes en Base64).
     *
     * <p>Recibe un {@link ClientRegistreDTO} con todos los datos necesarios para crear
     * un nuevo cliente. Las imágenes asociadas al documento identificativo y al carnet
     * de conducir viajan codificadas en Base64 dentro del propio JSON.</p>
     *
     * <p>Si el registro se realiza correctamente, devuelve el email del nuevo usuario
     * junto con un mensaje de confirmación. Si se produce un conflicto en el alta,
     * se responde con código HTTP 409.</p>
     *
     * @param registerDTO DTO con los datos de registro del cliente.
     * @return respuesta HTTP con el resultado del proceso de alta.
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody ClientRegistreDTO registerDTO) {

        try {
            // 1. Llamamos a la lógica pasándole el DTO (que ya incluye las fotos en Base64)
            Client nuevoCliente = clientService.registrarNouClient(registerDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuari registrat correctament");
            response.put("email", nuevoCliente.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ErrorAltaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // 409 Conflict

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error intern del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500
        }
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     *
     * <p>Si el correo existe en el sistema, se genera un token temporal y se envía
     * un correo con las instrucciones de recuperación. Si no existe, se responde
     * igualmente con éxito para no filtrar información del sistema.
     *
     * <p>Este comportamiento evita revelar si un email está registrado o no,
     * mejorando la seguridad del proceso de autenticación.</p>
     *
     * @param request petición con el email del usuario.
     * @return respuesta estándar del proceso de recuperación.
     */
    @PostMapping("/recover-password")
    public ResponseEntity<PasswordRecoveryResponse> recoverPassword(
            @RequestBody PasswordRecoveryRequest request
    ) {
        userLogic.recoverPassword(request.getEmail());

        return ResponseEntity.ok(
                new PasswordRecoveryResponse(
                        "recover_sent",
                        "If the email exists, you will receive recovery instructions shortly."
                )
        );
    }

    /**
     * Restablece la contraseña de un usuario utilizando un token de recuperación.
     *
     * <p>El token recibido debe corresponder a un proceso válido de recuperación.
     * Si el token es correcto y no ha caducado, se actualiza la contraseña del
     * usuario con el nuevo valor indicado en la petición.</p>
     *
     * @param request petición con token y nueva contraseña.
     * @return respuesta estándar informando del resultado del cambio.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordRecoveryResponse> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        userLogic.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(
                new PasswordRecoveryResponse(
                        "password_reset_ok",
                        "Your password has been updated successfully."
                )
        );
    }

    /**
     * Realiza el login del usuario.
     *
     * <p>Primero valida las credenciales mediante la lógica de negocio. Después,
     * recupera el usuario desde persistencia para construir el DTO de respuesta que
     * consumirá la aplicación móvil. Nunca devuelve la contraseña.
     *
     * <p>La respuesta incluye los datos básicos del usuario autenticado y un token
     * identificativo de sesión generado para el cliente consumidor.</p>
     *
     * @param loginRequest credenciales de acceso.
     * @return respuesta HTTP 200 con los datos de sesión o el error correspondiente.
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
       System.out.println("BACKEND RECIBE -> Email: [" + loginRequest.getEmail() + "] | Password: [" + loginRequest.getPassword() + "]");
        try {
            userLogic.login(loginRequest.getEmail(), loginRequest.getPassword());

            Usuari usuari = usuariRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new AccesDenegatException("Usuari no existeix"));

            LoginResponse response = new LoginResponse();
            response.setEmail(usuari.getEmail());
            response.setNomComplet(usuari.getNomComplet());
            response.setToken(UUID.randomUUID().toString());

            return ResponseEntity.ok(response);

        } catch (AccesDenegatException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error intern del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}