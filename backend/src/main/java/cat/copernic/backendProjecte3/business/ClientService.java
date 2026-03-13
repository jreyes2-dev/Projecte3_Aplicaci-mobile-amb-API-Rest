package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.dto.ClientUpdateDTO;
import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.dto.ClientRegistreDTO;
import cat.copernic.backendProjecte3.entities.Client;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.ErrorAltaException;
import cat.copernic.backendProjecte3.exceptions.ErrorDeleteException;
import cat.copernic.backendProjecte3.repository.ClientRepository;
import java.util.Base64;
// Puedes eliminar la importación de org.springframework.web.multipart.MultipartFile
import jakarta.transaction.Transactional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepo;

    public List<Client> obtenirTots() {
        return clientRepo.findAll();
    }

    public Client obtenirPerId(String email) {
        return clientRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Client no trobat amb email: " + email));
    }

    public Client obtenirPerDni(String dni) {
        return clientRepo.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Client no trobat amb DNI: " + dni));
    }

    @Transactional
    public void eliminarClient(String email) throws ErrorDeleteException {
        if (!clientRepo.existsById(email)) {
            throw new ErrorDeleteException("No es pot eliminar: " + email);
        }
        clientRepo.deleteById(email);
    }

    @Transactional
    public Client guardarClient(Client client) {
        return clientRepo.save(client);
    }

    /**
     * Registra un nuevo cliente validando duplicados y guardando fotos si existen.
     */
    /**
     * Registra un nuevo cliente validando duplicados y guardando fotos como BLOB.
     */
    @Transactional
    public Client registrarNouClient(ClientRegistreDTO dto) throws ErrorAltaException {

        // 1. Validar Email
        if (clientRepo.existsById(dto.getEmail())) {
            throw new ErrorAltaException("Ja existeix un usuari amb aquest email: " + dto.getEmail());
        }

        // 2. Validar DNI
        if (clientRepo.existsByDni(dto.getDni())) {
            throw new ErrorAltaException("Ja existeix un client amb aquest DNI: " + dto.getDni());
        }

        // 3. Mapear DTO -> Entidad
        Client nouClient = new Client();

        nouClient.setEmail(dto.getEmail());
        nouClient.setNomComplet(dto.getNomComplet());
        nouClient.setPassword(PasswordHasher.encode(dto.getPassword()));
        nouClient.setRol(UserRole.CLIENT);

        nouClient.setDni(dto.getDni());
        nouClient.setDataCaducitatDni(dto.getDataCaducitatDni());
        nouClient.setNacionalitat(dto.getNacionalitat());
        nouClient.setAdreca(dto.getAdreca());
        nouClient.setTipusCarnetConduir(dto.getTipusCarnetConduir());
        nouClient.setDataCaducitatCarnet(dto.getDataCaducitatCarnet());
        nouClient.setNumeroTargetaCredit(dto.getNumeroTargetaCredit());

        // 4. Convertir imágenes de Base64 (String) a byte[] (BLOB)
        if (dto.getImatgeDni() != null && !dto.getImatgeDni().isEmpty()) {
            nouClient.setImatgeDni(Base64.getDecoder().decode(dto.getImatgeDni()));
        }
        
        if (dto.getImatgeCarnet() != null && !dto.getImatgeCarnet().isEmpty()) {
            nouClient.setImatgeCarnet(Base64.getDecoder().decode(dto.getImatgeCarnet()));
        }
        
        if (dto.getFotoPerfil() != null && !dto.getFotoPerfil().isEmpty()) {
            nouClient.setFotoPerfil(Base64.getDecoder().decode(dto.getFotoPerfil()));
        }

        return clientRepo.save(nouClient);
    }

    /**
     * RF04: Actualizar perfil de cliente por DNI
     */
   /**
     * RF04: Actualizar perfil de cliente por Email
     */
    @Transactional
    public Client actualitzarPerfilPerEmail(String email, ClientUpdateDTO dto) {
        // 1. Buscamos al cliente por su email
        Client client = obtenirPerId(email); 

        // 2. Actualizamos los campos de texto
        client.setNomComplet(dto.getNomComplet());
        client.setTelefon(dto.getTelefon());
        client.setAdreca(dto.getAdreca());
        client.setNacionalitat(dto.getNacionalitat());
        client.setNumeroTargetaCredit(dto.getNumeroTargetaCredit());
        client.setDataCaducitatDni(dto.getDataCaducitatDni());
        client.setTipusCarnetConduir(dto.getTipusCarnetConduir());
        client.setDataCaducitatCarnet(dto.getDataCaducitatCarnet());

        // 3. ACTUALIZACIÓN DE IMÁGENES (Faltaba esto)
        // Solo las actualizamos si el móvil nos envía un Base64 nuevo (no nulo ni vacío)
        if (dto.getImatgeDni() != null && !dto.getImatgeDni().isEmpty()) {
            client.setImatgeDni(Base64.getDecoder().decode(dto.getImatgeDni()));
        }
        
        if (dto.getImatgeCarnet() != null && !dto.getImatgeCarnet().isEmpty()) {
            client.setImatgeCarnet(Base64.getDecoder().decode(dto.getImatgeCarnet()));
        }
        
        // AÑADIR ESTE BLOQUE PARA LA FOTO DE PERFIL:
        if (dto.getFotoPerfil() != null && !dto.getFotoPerfil().isEmpty()) {
            client.setFotoPerfil(Base64.getDecoder().decode(dto.getFotoPerfil()));
        }

        // 4. Guardamos en la base de datos
        return clientRepo.save(client);
    }

    /**
     * Función auxiliar para guardar archivos en el servidor
     */
    
}