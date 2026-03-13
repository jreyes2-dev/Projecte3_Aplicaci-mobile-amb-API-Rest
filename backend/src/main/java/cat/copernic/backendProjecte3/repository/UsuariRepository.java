package cat.copernic.backendProjecte3.repository;

import cat.copernic.backendProjecte3.entities.Usuari;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para la entidad {@link Usuari}.
 *
 * <p>Permite realizar operaciones CRUD sobre usuarios y consultas
 * específicas relacionadas con autenticación y recuperación de contraseña.</p>
 */
@Repository
public interface UsuariRepository extends JpaRepository<Usuari, String> {

    /**
     * Busca un usuario a partir de su correo electrónico.
     *
     * @param email email del usuario.
     * @return un {@link Optional} con el usuario encontrado, si existe.
     */
    Optional<Usuari> findByEmail(String email);

    /**
     * Busca un usuario a partir del token de recuperación de contraseña.
     *
     * <p>Se utiliza durante el proceso de reset de contraseña para validar
     * que el token recibido pertenece a un usuario existente.</p>
     *
     * @param resetPasswordToken token de recuperación almacenado.
     * @return un {@link Optional} con el usuario asociado al token, si existe.
     */
    Optional<Usuari> findByResetPasswordToken(String resetPasswordToken);
}