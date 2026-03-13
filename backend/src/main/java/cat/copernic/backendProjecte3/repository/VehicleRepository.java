/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositori encarregat de gestionar l'accés a la base de dades per a l'entitat
 * {@link Vehicle}.
 *
 * Aquesta interfície utilitza Spring Data JPA i permet realitzar operacions
 * CRUD automàtiques sobre la taula de vehicles.
 *
 * També inclou una consulta personalitzada per obtenir vehicles disponibles
 * dins d'un rang de dates determinat.
 *
 * @author manel
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    @Query("""
    SELECT v FROM Vehicle v
    WHERE (:tipus IS NULL OR v.tipusVehicle = :tipus)
    AND (:dies BETWEEN v.minDiesLloguer AND v.maxDiesLloguer)
    AND NOT EXISTS (
        SELECT r FROM Reserva r
        WHERE r.vehicle = v
        AND r.estat = 'ACTIVA'
        AND r.dataInici <= :fi
        AND r.dataFi >= :inici
    )
""")
    List<Vehicle> findDisponibles(
            @Param("inici") LocalDate inici,
            @Param("fi") LocalDate fi,
            @Param("tipus") TipusVehicle tipus,
            @Param("dies") long dies
    );
}
