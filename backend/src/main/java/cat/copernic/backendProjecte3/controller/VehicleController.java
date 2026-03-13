package cat.copernic.backendProjecte3.controller;

import cat.copernic.backendProjecte3.dto.VehicleResponseDTO;
import cat.copernic.backendProjecte3.business.VehicleService;
import cat.copernic.backendProjecte3.dto.VehicleMapper;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.exceptions.DadesNoTrobadesException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador REST encarregat de gestionar les peticions HTTP relacionades amb
 * els vehicles.
 *
 * Proporciona endpoints per obtenir la llista de vehicles i per cercar vehicles
 * disponibles segons un rang de dates.
 *
 * Aquest controlador utilitza el servei {@link VehicleService} per accedir a la
 * lògica de negoci.
 *
 * @author manel
 */
@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    private final VehicleService vehicleService;

    /**
     * Constructor del controlador de vehicles.
     *
     * @param vehicleService servei que gestiona la lògica de negoci dels
     * vehicles
     */
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Endpoint que retorna tots els vehicles disponibles al sistema.
     *
     * RF90 - Llistar vehicles.
     *
     * @return resposta HTTP amb la llista de vehicles en format DTO
     * @throws DadesNoTrobadesException si no existeixen vehicles al sistema
     */
    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> llistarVehicles()
            throws DadesNoTrobadesException {

        List<Vehicle> vehicles = vehicleService.obtenirTots();

        if (vehicles.isEmpty()) {
            throw new DadesNoTrobadesException("No hi ha vehicles disponibles");
        }

        List<VehicleResponseDTO> response = vehicles.stream()
                .map(VehicleMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint que permet cercar vehicles disponibles dins d'un interval de
     * dates.
     *
     * Aquest mètode rep les dates com a String, les converteix a LocalDate i
     * crida al servei per obtenir els vehicles disponibles.
     *
     * @param inici data d'inici del lloguer
     * @param fi data de finalització del lloguer
     * @param tipus tipus de vehicle (opcional)
     * @param codiPostal paràmetre reservat per possibles filtres futurs
     *
     * @return llista de vehicles disponibles en format DTO
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<VehicleResponseDTO>> cercarVehiclesDisponibles(
            @RequestParam String inici,
            @RequestParam String fi,
            @RequestParam(required = false) TipusVehicle tipus,
            @RequestParam(required = false) String codiPostal
    ) {

        LocalDate dataInici = LocalDate.parse(inici);
        LocalDate dataFi = LocalDate.parse(fi);

        List<Vehicle> vehicles = vehicleService.cercarVehiclesDisponibles(
                dataInici,
                dataFi,
                tipus
        );

        List<VehicleResponseDTO> response = vehicles.stream()
                .map(VehicleMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
