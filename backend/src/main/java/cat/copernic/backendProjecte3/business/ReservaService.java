package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.dto.CancelReservaResponse;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatReserva;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.*;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servei de negoci encarregat de gestionar tota la lògica relacionada amb les
 * reserves dins del sistema.
 *
 * Aquesta classe actua com a capa intermèdia entre el controlador
 * (ReservaController) i els repositoris de base de dades.
 *
 * Permet crear reserves, consultar reserves d'un client, obtenir reserves per
 * identificador i anul·lar reserves existents.
 *
 * També s'encarrega de validar: - permisos d'usuari - disponibilitat del
 * vehicle - rang de dates - límits de dies per vehicle
 *
 * @author manel
 */
@Service
public class ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private EmailService emailService;

    @Value("${reserva.cancel.fullRefundDays:3}")
    private int fullRefundDays;

    public ReservaService(
            ReservaRepository reservaRepository,
            VehicleRepository vehicleRepository,
            ClientRepository clientRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.vehicleRepository = vehicleRepository;
        this.clientRepository = clientRepository;
    }

    /**
     * Obté totes les reserves associades a un client concret.
     *
     * @param email correu electrònic del client
     * @return llista de reserves del client
     */
    public List<Reserva> obtenirPerClient(String email) {
        return reservaRepository.findByClient_Email(email);
    }

    /**
     * Obté una reserva concreta a partir del seu identificador.
     *
     * @param id identificador de la reserva
     * @return reserva trobada
     * @throws ReservaNoTrobadaException si la reserva no existeix
     */
    public Reserva obtenirPerId(Long id) throws ReservaNoTrobadaException {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNoTrobadaException("Reserva no trobada"));
    }

    /**
     * Crea una nova reserva per a un vehicle determinat.
     *
     * Aquest mètode valida: - el rol de l'usuari - que les dates siguin
     * correctes - que el vehicle estigui disponible - que els dies de reserva
     * compleixin els límits del vehicle
     *
     * També calcula el preu total de la reserva i guarda la informació a la
     * base de dades.
     *
     * @param emailClient email del client que fa la reserva
     * @param matricula matrícula del vehicle reservat
     * @param inici data d'inici de la reserva
     * @param fi data de finalització de la reserva
     * @param userName usuari que realitza l'acció
     *
     * @return reserva creada i guardada a la base de dades
     *
     * @throws ReservaDatesNoValidsException si les dates no són vàlides
     * @throws VehicleNoDisponibleException si el vehicle no està disponible
     * @throws AccesDenegatException si l'usuari no té permisos
     * @throws DadesNoTrobadesException si no es troben dades necessàries
     */

    @Transactional
    public Reserva crearReserva(String emailClient, String matricula, LocalDate inici, LocalDate fi, String userName)
            throws ReservaDatesNoValidsException, VehicleNoDisponibleException, AccesDenegatException, DadesNoTrobadesException {

        // Control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow(() -> new AccesDenegatException("Usuari no trobat o sense rol"));
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }

        // Validacio dates
        if (inici.isAfter(fi)) {
            throw new ReservaDatesNoValidsException("La data d'inici és abans que data final!!");
        }
        if (inici.isBefore(LocalDate.now())) {
            throw new ReservaDatesNoValidsException("Reserva en el passat!!");
        }

        // Validació disponibilitat
        List<Reserva> reserves = reservaRepository.findReservasSolapadas(matricula, inici, fi);
        if (!reserves.isEmpty()) {
            throw new ReservaDatesNoValidsException("Vehicle no disponible en aquestes dates");
        }

        // Client i vehicle
        Client client = clientRepository.findById(emailClient)
                .orElseThrow(() -> new DadesNoTrobadesException("Client no trobat"));

        Vehicle vehicle = vehicleRepository.findById(matricula)
                .orElseThrow(() -> new DadesNoTrobadesException("Vehicle no trobat"));

        if (vehicle.getEstatVehicle().equals(EstatVehicle.BAIXA)) {
            throw new VehicleNoDisponibleException("El vehicle està fora de servei");
        }

        // --- NOU: VALIDAR LÍMITS REALS DEL VEHICLE ---
        long dies = ChronoUnit.DAYS.between(inici, fi);
        dies = (dies <= 0 ? 1 : dies);

        if (dies < vehicle.getMinDiesLloguer() || dies > vehicle.getMaxDiesLloguer()) {
            throw new ReservaDatesNoValidsException(
                    "Els dies seleccionats (" + dies + ") no estan permesos per a aquest vehicle. Límits: "
                    + vehicle.getMinDiesLloguer() + " - " + vehicle.getMaxDiesLloguer() + " dies."
            );
        }

        // Creem reserva
        Reserva reserva = new Reserva();
        reserva.setClient(client);
        reserva.setVehicle(vehicle);
        reserva.setDataInici(inici);
        reserva.setDataFi(fi);

        // Calculem dies entre dates
        dies = ChronoUnit.DAYS.between(inici, fi);
        dies = (dies == 0 ? 1 : dies);

        // El preu hora per 24 hores i pels dies de lloguer
        BigDecimal importTotal = vehicle.getPreuHora()
                .multiply(new BigDecimal(24))
                .multiply(new BigDecimal(dies));

        reserva.setImportTotal(importTotal);
        reserva.setFiancaPagada(vehicle.getFiancaEstandard());
        reserva.setEstat(EstatReserva.ACTIVA);

        // Guardem a la base de dades
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // ENVIAMENT DEL CORREU REAL D'ALTA
        try {
            emailService.sendReservationCreatedEmail(
                    client.getEmail(),
                    client.getNomComplet(),
                    vehicle.getMatricula(),
                    inici.toString(),
                    fi.toString(),
                    "RES-" + reservaGuardada.getIdReserva()
            );
        } catch (Exception e) {
            System.err.println("Error enviant el correu d'alta de reserva: " + e.getMessage());
        }

        return reservaGuardada;
    }

    /**
     * Anul·la una reserva existent.
     *
     * El sistema comprova: - permisos de l'usuari - que la reserva encara no
     * hagi començat
     *
     * Si la cancel·lació es fa amb prou antelació, es calcula el reemborsament
     * corresponent.
     *
     * @param idReserva identificador de la reserva
     * @param userName usuari que realitza la cancel·lació
     *
     * @return informació sobre el resultat de la cancel·lació
     *
     * @throws ReservaNoTrobadaException si la reserva no existeix
     * @throws AccesDenegatException si l'usuari no té permisos
     * @throws ReservaNoCancelableException si la reserva no es pot cancel·lar
     */
    @Transactional
    public CancelReservaResponse anularReserva(Long idReserva, String userName)
            throws ReservaNoTrobadaException, AccesDenegatException, ReservaNoCancelableException {

        // Control de ROL
        UserRole rol = userLogic.getRole(userName).orElseThrow(() -> new AccesDenegatException("Usuari no trobat o sense rol"));
        if (rol != UserRole.CLIENT && rol != UserRole.AGENT && rol != UserRole.ADMIN) {
            throw new AccesDenegatException("Rol no vàlid");
        }

        Reserva reserva = obtenirPerId(idReserva);

        LocalDate today = LocalDate.now();
        LocalDate inici = reserva.getDataInici();

        if (!today.isBefore(inici)) {
            throw new ReservaNoCancelableException("No es pot anul·lar una reserva iniciada o finalitzada.");
        }

        long daysAhead = ChronoUnit.DAYS.between(today, inici);
        BigDecimal refund = BigDecimal.ZERO;

        if (daysAhead >= fullRefundDays) {
            BigDecimal importTotal = reserva.getImportTotal() != null ? reserva.getImportTotal() : BigDecimal.ZERO;
            BigDecimal fianca = reserva.getFiancaPagada() != null ? reserva.getFiancaPagada() : BigDecimal.ZERO;
            refund = importTotal.add(fianca);
        }

        reserva.setEstat(EstatReserva.CANCELADA);
        reservaRepository.save(reserva);

        try {
            emailService.sendReservationCancelledEmail(
                    reserva.getClient().getEmail(),
                    reserva.getClient().getNomComplet(),
                    reserva.getVehicle().getMatricula(),
                    "RES-" + idReserva,
                    refund.doubleValue()
            );
        } catch (Exception e) {
            System.err.println("Error enviant el correu d'anul·lació: " + e.getMessage());
        }

        String msg = (refund.compareTo(BigDecimal.ZERO) > 0)
                ? "Reserva anul·lada. Reemborsament: " + refund + " €"
                : "Reserva anul·lada. Sense reemborsament.";

        return new CancelReservaResponse(idReserva, refund, msg);
    }
}
