package cat.copernic.backendProjecte3;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.enums.Reputacio;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import cat.copernic.backendProjecte3.business.ClientService;
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class ClientTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepo;

    @BeforeAll
    public void init() {
        clientRepo.deleteAll();
    }

    @Test
    public void testRegistrarNouClient_FluxCorrecte() {

        ClientRegistreDTO dto = new ClientRegistreDTO();
        dto.setEmail("nou.client@test.com");
        dto.setPassword("secret123");
        dto.setNomComplet("Client de Prova");
        dto.setDni("12345678Z");
        dto.setAdreca("Carrer de l'Exemple, 123");
        dto.setTipusCarnetConduir("B");
        dto.setNumeroTargetaCredit("1111-2222-3333-4444");

        // AQUÍ HEMOS QUITADO LOS "null, null"
        Client resultat = assertDoesNotThrow(() ->
                clientService.registrarNouClient(dto)
        );

        assertNotNull(resultat);

        Client clientDesat = clientRepo.findById("nou.client@test.com").orElse(null);

        // Verificaciones
        assertNotNull(clientDesat);
        assertEquals("nou.client@test.com", clientDesat.getEmail());
        assertTrue(PasswordHasher.check("secret123", clientDesat.getPassword()));
        assertEquals("12345678Z", clientDesat.getDni());
        assertEquals("Carrer de l'Exemple, 123", clientDesat.getAdreca());
        assertEquals(UserRole.CLIENT, clientDesat.getRol());
    }

    @Test
    public void testRegistrarClient_EmailDuplicat() throws ErrorAltaException {

        ClientRegistreDTO primer = new ClientRegistreDTO();
        primer.setEmail("ja.existeix@test.com");
        primer.setPassword("1234");
        primer.setNomComplet("Client Existent");
        primer.setDni("99999999X");

        // AQUÍ TAMBIÉN QUITAMOS LOS "null, null"
        clientService.registrarNouClient(primer);

        ClientRegistreDTO duplicat = new ClientRegistreDTO();
        duplicat.setEmail("ja.existeix@test.com"); // mismo email
        duplicat.setPassword("novaPass");
        duplicat.setNomComplet("Client Nou");
        duplicat.setDni("88888888Y"); // DNI diferente

        // Y AQUÍ TAMBIÉN QUITAMOS LOS "null, null"
        assertThrows(ErrorAltaException.class, () ->
                clientService.registrarNouClient(duplicat)
        );
    }
}
