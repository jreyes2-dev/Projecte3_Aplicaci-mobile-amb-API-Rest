package cat.copernic.backendProjecte3.dto;

import cat.copernic.backendProjecte3.entities.Client;
import java.time.LocalDate;
import java.util.Base64;

/**
 * DTO que representa la información del perfil de un cliente.
 *
 * <p>Se utiliza para transferir al frontend o a la app móvil los datos
 * personales, de documentación y de conducción del cliente sin exponer
 * directamente la entidad de base de datos.</p>
 *
 * <p>Las imágenes del DNI y del carnet se envían en formato Base64
 * para facilitar su transporte en respuestas JSON.</p>
 */
public class ClientProfileDTO {

    /** Documento nacional de identidad del cliente. */
    public String dni;

    /** Nombre completo del cliente. */
    public String nomComplet;

    /** Correo electrónico del cliente. */
    public String email;

    /** Número de teléfono del cliente. */
    public String telefon;

    /** Dirección postal del cliente. */
    public String adreca;

    /** Nacionalidad del cliente. */
    public String nacionalitat;

    /** Número de tarjeta de crédito asociado al cliente. */
    public String numeroTargetaCredit;

    /** Fecha de caducidad del DNI. */
    public LocalDate dataCaducitatDni;

    /** Tipo de carnet de conducir del cliente. */
    public String tipusCarnetConduir;

    /** Fecha de caducidad del carnet de conducir. */
    public LocalDate dataCaducitatCarnet;

    /**
     * Imagen del DNI codificada en Base64.
     *
     * <p>Será {@code null} si el cliente no tiene imagen registrada.</p>
     */
    public String imatgeDni;

    /**
     * Imagen del carnet de conducir codificada en Base64.
     *
     * <p>Será {@code null} si el cliente no tiene imagen registrada.</p>
     */
    public String imatgeCarnet;
    
    public String fotoPerfil;

    /**
     * Convierte una entidad {@link Client} en un DTO de perfil.
     *
     * <p>Este método copia los datos básicos del cliente y transforma las
     * imágenes binarias almacenadas en base de datos a texto en formato
     * Base64 para poder enviarlas en una respuesta JSON.</p>
     *
     * @param c entidad del cliente origen.
     * @return DTO con los datos del perfil del cliente.
     */
    public static ClientProfileDTO from(Client c) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.dni = c.getDni();
        dto.nomComplet = c.getNomComplet();
        dto.email = c.getEmail();
        dto.telefon = c.getTelefon();
        dto.adreca = c.getAdreca();
        dto.nacionalitat = c.getNacionalitat();
        dto.numeroTargetaCredit = c.getNumeroTargetaCredit();
        dto.dataCaducitatDni = c.getDataCaducitatDni();
        dto.tipusCarnetConduir = c.getTipusCarnetConduir();
        dto.dataCaducitatCarnet = c.getDataCaducitatCarnet();

        if (c.getImatgeDni() != null) {
            dto.imatgeDni = Base64.getEncoder().encodeToString(c.getImatgeDni());
        } else {
            dto.imatgeDni = null;
        }

        if (c.getImatgeCarnet() != null) {
            dto.imatgeCarnet = Base64.getEncoder().encodeToString(c.getImatgeCarnet());
        } else {
            dto.imatgeCarnet = null;
        }
        
        if (c.getFotoPerfil() != null) {
            dto.fotoPerfil = Base64.getEncoder().encodeToString(c.getFotoPerfil());
        } else {
            dto.fotoPerfil = null;
        }

        return dto;
    }
}