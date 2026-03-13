package cat.copernic.backendProjecte3.dto;

import cat.copernic.backendProjecte3.entities.Vehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import cat.copernic.backendProjecte3.enums.EstatVehicle;
import java.util.Base64;

/**
 * Classe encarregada de convertir objectes de tipus {@link Vehicle} en objectes
 * DTO ({@link VehicleResponseDTO}) que poden ser enviats al frontend.
 *
 * Aquest mapper evita exposar directament les entitats de la base de dades a la
 * capa de presentació.
 *
 * També realitza la conversió de la imatge del vehicle des de format binari
 * (byte[]) a Base64 per poder ser enviada correctament en format JSON.
 */
public class VehicleMapper {

    /**
     * Converteix una entitat Vehicle en un DTO VehicleResponseDTO.
     *
     * @param vehicle entitat Vehicle procedent de la base de dades
     * @return objecte DTO amb la informació del vehicle
     */
    // Convierte de Entity a ResponseDTO (para enviar al frontend)
    public static VehicleResponseDTO toDTO(Vehicle vehicle) {
        VehicleResponseDTO dto = new VehicleResponseDTO();

        dto.setMatricula(vehicle.getMatricula());
        dto.setMarca(vehicle.getMarca());
        dto.setModel(vehicle.getModel());
        dto.setVariant(vehicle.getVariant());
        dto.setPreuHora(vehicle.getPreuHora());
        dto.setFotoUrl(vehicle.getFotoUrl());
        dto.setPotencia(vehicle.getPotencia());
        dto.setColor(vehicle.getColor());
        dto.setLimitQuilometratge(vehicle.getLimitQuilometratge());
        dto.setFiancaEstandard(vehicle.getFiancaEstandard());
        dto.setMinDiesLloguer(vehicle.getMinDiesLloguer());
        dto.setMaxDiesLloguer(vehicle.getMaxDiesLloguer());

        // NUEVO: Convertir BLOB a Base64 para el frontend
        if (vehicle.getFotoBinario() != null) {
            dto.setFotoBase64(Base64.getEncoder().encodeToString(vehicle.getFotoBinario()));
        }

        // Enums como String
        if (vehicle.getTipusVehicle() != null) {
            dto.setTipusVehicle(vehicle.getTipusVehicle().name());
        }
        if (vehicle.getEstatVehicle() != null) {
            dto.setEstatVehicle(vehicle.getEstatVehicle().name());
        }

        return dto;
    }
}
