package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.entities.Reserva;
import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.Reputacio;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.repository.ReservaRepository;
import cat.copernic.backendProjecte3.repository.VehicleRepository;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendProjecte3Application implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ReservaRepository reservaRepo;

    public static void main(String[] args) {
        SpringApplication.run(BackendProjecte3Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Inserint dades de prova COMPLETES per a la DEMO...");

        try {
            // Netegem la base de dades per evitar duplicats a cada reinici
            reservaRepo.deleteAll();
            vehicleRepo.deleteAll();

            // 1. Creació de CLIENTS (amb totes les dades administratives)
            Client client1 = crearClientExemple(
                    "bharrak93@gmail.com", "Maria Garcia", "44556677D",
                    "src/main/resources/demo-imatges/FotodePerfil1.png",
                    "src/main/resources/demo-imatges/maria_dni.jpg",
                    "src/main/resources/demo-imatges/maria_carnet.jpg",
                    LocalDate.of(2030, 5, 15), "España", "Carrer Major, 15, Terrassa", "666555444", 
                    "B", LocalDate.of(2032, 8, 20), "1111-2222-3333-44445"
            );

            Client client2 = crearClientExemple(
                    "joan@test.com", "Joan Piñol", "11223344X",
                    "src/main/resources/demo-imatges/FotodePerfil2.png",
                    "src/main/resources/demo-imatges/maria_dni.jpg", // Reutilitzem imatges per la demo
                    "src/main/resources/demo-imatges/maria_carnet.jpg",
                    LocalDate.of(2028, 11, 30), "Andorra", "Avinguda Meritxell, 10, Andorra", "777888999", 
                    "A2", LocalDate.of(2029, 1, 10), "5555-6666-7777-88889"
            );

            Client client3 = crearClientExemple(
                    "laura@test.com", "Laura Vila", "94887766Z",
                    "src/main/resources/demo-imatges/FotoDePerfil3.png",
                    "src/main/resources/demo-imatges/maria_dni.jpg",
                    "src/main/resources/demo-imatges/maria_carnet.jpg",
                    LocalDate.of(2035, 2, 28), "Francia", "Rue de la Paix, 5, Paris", "600111222", 
                    "B1", LocalDate.of(2034, 12, 1), "9999-0000-1111-22227"
            );

            // 2. Creació de VEHICLES (amb dades tècniques completes)
            Vehicle tesla = crearVehicleExemple(
                    "1111AAA", "Tesla", "Model 3", "Elèctric", "25.00", "400.00", 
                    "src/main/resources/demo-imatges/Tesla_Model_3.png",
                    "350 CV", "Blanc", 500
            );

            Vehicle toyota = crearVehicleExemple(
                    "2222BBB", "Toyota", "Corolla", "Híbrid", "15.00", "200.00", 
                    "src/main/resources/demo-imatges/Toyota_Corolla.jpg",
                    "122 CV", "Gris", 800
            );

            Vehicle seat = crearVehicleExemple(
                    "3333CCC", "Seat", "Ibiza", "Combustió", "10.00", "150.00", 
                    "src/main/resources/demo-imatges/Seat_Ibiza.jpg",
                    "95 CV", "Vermell", 1200
            );

            // 3. Creació de RESERVES
            crearReservaExemple(client1, tesla, 1, 3, "150.00", "400.00");
            crearReservaExemple(client2, tesla, 10, 2, "100.00", "400.00");

            crearReservaExemple(client3, toyota, 5, 4, "120.00", "200.00");
            crearReservaExemple(client1, toyota, 15, 1, "30.00", "200.00");

            crearReservaExemple(client2, seat, 2, 5, "100.00", "150.00");
            crearReservaExemple(client3, seat, 20, 2, "40.00", "150.00");

            System.out.println("Dades inserides correctament! La Demo està a punt i completa.");
        } catch (Exception e) {
            System.err.println("ERROR inserint dades de prova: " + e.getMessage());
        }
    }

    private byte[] llegirImatge(String rutaArxiu) {
        if (rutaArxiu == null || rutaArxiu.isEmpty()) return null;
        try {
            File arxiu = new File(rutaArxiu);
            if (arxiu.exists()) {
                return Files.readAllBytes(arxiu.toPath());
            } else {
                System.out.println("AVÍS: No s'ha trobat la imatge " + rutaArxiu + " - Guardant null.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error llegint la imatge " + rutaArxiu + ": " + e.getMessage());
            return null;
        }
    }

    // Mètode actualitzat amb TOTS els camps de Client
    private Client crearClientExemple(
            String email, String nom, String dni, String imgPerfil, String imgDni, String imgCarnet,
            LocalDate dataCadDni, String nacionalitat, String adreca, String telefon,
            String tipusCarnet, LocalDate dataCadCarnet, String targetaCredit) {
        
        Client c = clientRepo.findById(email).orElse(new Client());

        // Dades d'Usuari
        c.setEmail(email);
        if (c.getPassword() == null) {
            c.setPassword(PasswordHasher.encode("123456"));
        }
        c.setNomComplet(nom);
        c.setRol(UserRole.CLIENT);

        // Dades de Client (Identificació)
        c.setDni(dni);
        c.setDataCaducitatDni(dataCadDni);
        c.setNacionalitat(nacionalitat);
        c.setAdreca(adreca);
        c.setTelefon(telefon);
        
        // Dades de Conducció
        c.setTipusCarnetConduir(tipusCarnet);
        c.setDataCaducitatCarnet(dataCadCarnet);
        
        // Dades Econòmiques i de Sistema
        c.setNumeroTargetaCredit(targetaCredit);
        c.setReputacio(Reputacio.PREMIUM);

        // Imatges
        c.setImatgeDni(llegirImatge(imgDni));
        c.setImatgeCarnet(llegirImatge(imgCarnet));
        c.setFotoPerfil(llegirImatge(imgPerfil));
        // NOTA: Falta c.setImatgePerfil(llegirImatge(imgPerfil)) si decideixes afegir-ho a l'entitat.

        return clientRepo.save(c);
    }

    // Mètode actualitzat amb TOTS els camps de Vehicle
    private Vehicle crearVehicleExemple(
            String matricula, String marca, String model, String variant, 
            String preu, String fianca, String rutaImatge,
            String potencia, String color, Integer limitKm) {
        
        Vehicle v = new Vehicle();
        
        // Dades base
        v.setMatricula(matricula);
        v.setMarca(marca);
        v.setModel(model);
        v.setVariant(variant);
        v.setTipusVehicle(TipusVehicle.COTXE);
        v.setEstatVehicle(EstatVehicle.ALTA);
        
        // Dades tècniques
        v.setPotencia(potencia);
        v.setColor(color);
        v.setLimitQuilometratge(limitKm);
        
        // Dades econòmiques
        v.setPreuHora(new BigDecimal(preu));
        v.setFiancaEstandard(new BigDecimal(fianca));
        v.setMinDiesLloguer(1);
        v.setMaxDiesLloguer(30);

        // Imatge
        v.setFotoBinario(llegirImatge(rutaImatge));

        return vehicleRepo.save(v);
    }

    private void crearReservaExemple(Client c, Vehicle v, int offsetDiesInici, int duradaDies, String importTotal, String fianca) {
        Reserva r = new Reserva();
        r.setClient(c);
        r.setVehicle(v);

        LocalDate dataInici = LocalDate.now().plusDays(offsetDiesInici);
        LocalDate dataFi = dataInici.plusDays(duradaDies);

        r.setDataInici(dataInici);
        r.setDataFi(dataFi);
        r.setImportTotal(new BigDecimal(importTotal));
        r.setFiancaPagada(new BigDecimal(fianca));
        
        reservaRepo.save(r);
    }
}
