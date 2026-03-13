/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servei de negoci encarregat de gestionar les operacions relacionades amb els
 * vehicles dins del sistema.
 *
 * Aquesta classe actua com a capa intermèdia entre el controlador
 * (VehicleController) i el repositori (VehicleRepository).
 *
 * Permet obtenir tots els vehicles del sistema i cercar vehicles disponibles en
 * funció d'un rang de dates i opcionalment pel tipus de vehicle.
 *
 * @author manel
 */
@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;

    /**
     * Constructor del servei de vehicles.
     *
     * @param vehicleRepository repositori encarregat de gestionar l'accés a la
     * base de dades dels vehicles
     */
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Obté tots els vehicles registrats a la base de dades.
     *
     * @return llista de tots els vehicles disponibles al sistema
     */
    public List<Vehicle> obtenirTots() {
        return vehicleRepository.findAll();
    }

    /**
     * Cerca vehicles disponibles dins d'un rang de dates determinat.
     *
     * Aquesta funció calcula el nombre de dies entre les dues dates i aplica la
     * política de negoci del sistema: el lloguer mínim és d'almenys 1 dia.
     *
     * Posteriorment delega la consulta al repositori per obtenir els vehicles
     * que compleixen les condicions de disponibilitat.
     *
     * @param inici data d'inici del lloguer
     * @param fi data de finalització del lloguer
     * @param tipus tipus de vehicle (opcional)
     *
     * @return llista de vehicles disponibles per al període indicat
     */
    public List<Vehicle> cercarVehiclesDisponibles(LocalDate inici, LocalDate fi, TipusVehicle tipus) {

        long dies = java.time.temporal.ChronoUnit.DAYS.between(inici, fi);

        // Política de negocio: mínimo 1 día
        if (dies <= 0) {
            dies = 1;
        }

        // Llamamos al repo pasándole los días calculados para que filtre bien
        return vehicleRepository.findDisponibles(inici, fi, tipus, dies);
    }
}
