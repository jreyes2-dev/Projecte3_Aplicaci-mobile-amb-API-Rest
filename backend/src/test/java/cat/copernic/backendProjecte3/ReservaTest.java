/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.ReservaDatesNoValidsException;
import cat.copernic.backendProjecte3.exceptions.VehicleNoDisponibleException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;
import cat.copernic.backendProjecte3.business.ReservaService;
import cat.copernic.backendProjecte3.business.UserLogic;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author manel
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReservaTest {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ReservaRepository reservaRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private VehicleRepository vehicleRepo;

    // Components per fer el Login REAL
    @Autowired
    private UserLogic userLogic;

    private Client clientTest;
    private Vehicle vehicleTest;
    private Reserva reservaTest;

    // Dades per fer escenari1
    private final String EMAIL_LOGIN = "client.real@test.com";
    private final String PASSWORD_RAW = "passwordSegur123";

    @BeforeAll
    public void setupGlobal() {
        reservaRepo.deleteAll();
        clientRepo.deleteAll();
        vehicleRepo.deleteAll();
    }

    /**
     * *
     * - Rol client - Vehicle disponible
     */
    public void escenari1() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setNomComplet("Usuario de Prueba");
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setMarca("Yamaha");
        vehicleTest.setModel("MT-07");
        vehicleTest.setVariant("Gasolina");
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);
    }

    /**
     * *
     * - Rol client - Vehicle de baixa
     */
    public void escenari2() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setNomComplet("Usuario de Prueba");
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setMarca("Yamaha");
        vehicleTest.setModel("MT-07");
        vehicleTest.setVariant("Gasolina");
        vehicleTest.setEstatVehicle(EstatVehicle.BAIXA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);
    }

    /**
     * *
     * - Rol NONE - Vehicle disponible
     */
    public void escenari3() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.NONE);
        clientTest.setDni("12345678A");
        clientTest.setNomComplet("Usuario de Prueba");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setMarca("Yamaha");
        vehicleTest.setModel("MT-07");
        vehicleTest.setVariant("Gasolina");
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);
    }

    /**
     * *
     * - Rol AGENT - Vehicle disponible - reserva OK
     */
    public void escenari4() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.AGENT);
        clientTest.setNomComplet("Usuario de Prueba");
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setMarca("Yamaha");
        vehicleTest.setModel("MT-07");
        vehicleTest.setVariant("Gasolina");
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);

        //reserva ok
        assertDoesNotThrow(() -> {
            this.reservaTest = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    /**
     * *
     * - Rol CLIENT - Vehicle disponible - reserva OK
     */
    public void escenari5() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setNomComplet("Usuario de Prueba");
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setMarca("Yamaha");
        vehicleTest.setModel("MT-07");
        vehicleTest.setVariant("Gasolina");
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);

        //reserva ok        
        assertDoesNotThrow(() -> {
            this.reservaTest = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    /**
     * *
     * - Rol CLIENT - Vehicle disponible - reserva OK
     */
    public void escenari6() {
        //client
        clientTest = new Client();
        clientTest.setEmail(EMAIL_LOGIN);
        clientTest.setPassword(PasswordHasher.encode(PASSWORD_RAW)); // Encriptem!
        clientTest.setRol(UserRole.CLIENT);
        clientTest.setNomComplet("Usuario de Prueba");
        clientTest.setDni("12345678A");
        clientRepo.save(clientTest);

        vehicleTest = new Vehicle();
        vehicleTest.setMatricula("9999-MAD");
        vehicleTest.setTipusVehicle(TipusVehicle.MOTO);
        vehicleTest.setMarca("Yamaha");
        vehicleTest.setModel("MT-07");
        vehicleTest.setVariant("Gasolina");
        vehicleTest.setEstatVehicle(EstatVehicle.ALTA);
        vehicleTest.setPreuHora(new BigDecimal(15.55f));
        vehicleRepo.save(vehicleTest);

        //reserva ok
        assertDoesNotThrow(() -> {
            this.reservaTest = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    @Test
    public void crearReserva_Ok() {

        escenari1();

        assertDoesNotThrow(() -> {
            Reserva resultat = reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );

            assertNotNull(resultat);
            assertEquals(EMAIL_LOGIN, resultat.getClient().getUsername());

        });

    }

    @Test
    public void crearReserva_VehicleBaixa() {

        escenari2();

        assertThrows(VehicleNoDisponibleException.class, () -> {
            reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    @Test
    public void crearReserva_RolNoautoritzat() {

        escenari3();

        assertThrows(AccesDenegatException.class, () -> {
            reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    clientTest.getUsername()
            );
        });
    }

    /**
     * *
     * Es eeserva un vehicle i llavors es reserva de nou en dates no compatibles
     */
    @Test
    public void crearReserva_VehicleJaReservat() {

        escenari6();

        assertThrows(ReservaDatesNoValidsException.class, () -> {
            reservaService.crearReserva(
                    EMAIL_LOGIN,
                    vehicleTest.getMatricula(),
                    reservaTest.getDataFi().minusDays(1),
                    reservaTest.getDataFi().plusDays(10),
                    clientTest.getUsername()
            );
        });
    }
}
