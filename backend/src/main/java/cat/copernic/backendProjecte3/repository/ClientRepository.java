package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para la entidad {@link Client}.
 *
 * <p>Extiende de {@link JpaRepository} para disponer de las operaciones
 * básicas de persistencia y añade consultas específicas relacionadas
 * con el DNI del cliente.</p>
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    /**
     * Busca un cliente por su DNI.
     *
     * @param dni documento nacional de identidad del cliente.
     * @return un {@link Optional} con el cliente si existe.
     */
    Optional<Client> findByDni(String dni);

    /**
     * Comprueba si ya existe un cliente con el DNI indicado.
     *
     * @param dni documento nacional de identidad a comprobar.
     * @return {@code true} si existe un cliente con ese DNI,
     *         {@code false} en caso contrario.
     */
    boolean existsByDni(String dni);
}