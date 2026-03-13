package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.dto.ClientProfileDTO;
import cat.copernic.backendProjecte3.dto.ClientUpdateDTO;
import cat.copernic.backendProjecte3.business.ClientService;
import cat.copernic.backendProjecte3.entities.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST encargado de la gestión del perfil de los clientes.
 *
 * <p>Esta clase expone los endpoints necesarios para consultar y actualizar
 * la información de un cliente a partir de su correo electrónico.</p>
 *
 * <p>Su responsabilidad principal es actuar como capa intermedia entre
 * las peticiones HTTP y la lógica de negocio implementada en
 * {@link ClientService}.</p>
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    /**
     * Servicio de negocio utilizado para consultar y modificar datos de clientes.
     */
    private final ClientService clientService;

    /**
     * Constructor del controlador de clientes.
     *
     * @param clientService servicio encargado de la lógica de negocio de clientes.
     */
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Obtiene el perfil de un cliente a partir de su email.
     *
     * <p>Recupera la entidad {@link Client} desde la capa de servicio y la transforma
     * a un {@link ClientProfileDTO} para devolver únicamente la información necesaria
     * al cliente consumidor de la API.</p>
     *
     * @param email correo electrónico del cliente que se quiere consultar.
     * @return respuesta HTTP con los datos del perfil del cliente.
     */
    @GetMapping("/{email}")
    public ResponseEntity<ClientProfileDTO> getClient(@PathVariable String email) {
        Client client = clientService.obtenirPerId(email);
        return ResponseEntity.ok(ClientProfileDTO.from(client));
    }

    /**
     * Actualiza la información del perfil de un cliente.
     *
     * <p>Recibe el email del cliente por URL y un DTO con los nuevos datos del perfil.
     * La actualización se delega a la capa de servicio, que se encarga de aplicar
     * los cambios y persistirlos en base de datos.</p>
     *
     * @param email correo electrónico del cliente que se quiere actualizar.
     * @param dto objeto con los nuevos datos del perfil.
     * @return respuesta HTTP con el perfil actualizado del cliente.
     */
    @PutMapping("/{email}")
    public ResponseEntity<ClientProfileDTO> updateClient(
            @PathVariable String email,
            @RequestBody ClientUpdateDTO dto) {

        Client updated = clientService.actualitzarPerfilPerEmail(email, dto);

        return ResponseEntity.ok(ClientProfileDTO.from(updated));
    }
}