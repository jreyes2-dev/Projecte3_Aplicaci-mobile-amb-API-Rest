package cat.copernic.backendProjecte3.dto;

import cat.copernic.backendProjecte3.entities.Reserva;
import java.time.LocalDate;
import java.util.Base64;

/**
 * DTO utilitzat per enviar la informació d'una reserva des del backend cap al
 * frontend.
 *
 * Inclou les dades principals de la reserva així com informació del vehicle
 * associat.
 *
 * També inclou la imatge del vehicle en format Base64 per poder ser mostrada
 * directament a l'aplicació Android.
 */
public class ReservaResponse {

    private Long idReserva;
    private LocalDate dataInici;
    private LocalDate dataFi;
    private String clientEmail;
    private String vehicleMatricula;
    private String importTotal;
    private String fiancaPagada;
    private String estat; // <-- AFEGIT PER ENVIAR L'ESTAT A ANDROID
    private String vehicleFotoBase64;

    // GETTERS
    public Long getIdReserva() {
        return idReserva;
    }

    public LocalDate getDataInici() {
        return dataInici;
    }

    public LocalDate getDataFi() {
        return dataFi;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getVehicleMatricula() {
        return vehicleMatricula;
    }

    public String getImportTotal() {
        return importTotal;
    }

    public String getFiancaPagada() {
        return fiancaPagada;
    }

    public String getEstat() {
        return estat;
    }

    public String getVehicleFotoBase64() {
        return vehicleFotoBase64;
    }

    public static ReservaResponse fromEntity(Reserva r) {
        ReservaResponse dto = new ReservaResponse();
        dto.idReserva = r.getIdReserva();
        dto.dataInici = r.getDataInici();
        dto.dataFi = r.getDataFi();
        dto.clientEmail = r.getClient() != null ? r.getClient().getUsername() : null;
        dto.vehicleMatricula = r.getVehicle() != null ? r.getVehicle().getMatricula() : null;
        dto.importTotal = r.getImportTotal() != null ? r.getImportTotal().toPlainString() : null;
        dto.fiancaPagada = r.getFiancaPagada() != null ? r.getFiancaPagada().toPlainString() : null;
        // Llegim l'estat de l'entitat Reserva
        dto.estat = r.getEstat() != null ? r.getEstat().name() : "ACTIVA";

        if (r.getVehicle() != null && r.getVehicle().getFotoBinario() != null) {
            dto.vehicleFotoBase64 = Base64.getEncoder().encodeToString(r.getVehicle().getFotoBinario());
        }

        return dto;
    }
}
