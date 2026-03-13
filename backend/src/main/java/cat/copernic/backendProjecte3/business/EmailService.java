package cat.copernic.backendProjecte3.business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ---------------------------------------------------------
    // 1. CORREO RECUPERAR CONTRASEÑA (Código de tu compañero)
    // ---------------------------------------------------------
    public void sendPasswordRecoveryEmail(String to, String userName, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("AppVehicles - Password recovery");

        message.setText(
                "Hello " + userName + ",\n\n" +
                "We received a request to recover your password.\n\n" +
                "Use this recovery token in the mobile app:\n\n" +
                token + "\n\n" +
                "This token expires in 30 minutes.\n\n" +
                "If you did not request it, ignore this email.\n\n" +
                "MobileCat Team"
        );

        mailSender.send(message);
    }

    // ---------------------------------------------------------
    // 2. CORREO ALTA DE RESERVA (Nuevo)
    // ---------------------------------------------------------
    public void sendReservationCreatedEmail(String to, String userName, String matricula, String fechaInicio, String fechaFin, String codigoReserva) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("MobileCat - Confirmació de Reserva [" + codigoReserva + "]");
        
        message.setText(
                "Hola " + userName + ",\n\n" +
                "La teva reserva s'ha realitzat correctament.\n\n" +
                "Detalls de la reserva:\n" +
                "- Codi de reserva: " + codigoReserva + "\n" +
                "- Vehicle (Matrícula): " + matricula + "\n" +
                "- Data d'inici: " + fechaInicio + "\n" +
                "- Data de finalització: " + fechaFin + "\n\n" +
                "Gràcies per confiar en MobileCat!"
        );
        
        mailSender.send(message);
    }

    // ---------------------------------------------------------
    // 3. CORREO ANULACIÓN DE RESERVA (Nuevo)
    // ---------------------------------------------------------
    public void sendReservationCancelledEmail(String to, String userName, String matricula, String codigoReserva, double importRetornat) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("MobileCat - Reserva Anul·lada [" + codigoReserva + "]");
        
        // Lógica de devolución de importe solicitada en el enunciado
        String textRetorn = (importRetornat > 0) 
                ? "D'acord amb la nostra política, se t'ha retornat un import de: " + importRetornat + " €." 
                : "A causa de la proximitat de la data, no procedeix cap devolució d'import.";

        message.setText(
                "Hola " + userName + ",\n\n" +
                "Et confirmem que la teva reserva " + codigoReserva + " pel vehicle " + matricula + " ha estat anul·lada correctament.\n\n" +
                textRetorn + "\n\n" +
                "Esperem veure't aviat,\n" +
                "MobileCat Team"
        );
        
        mailSender.send(message);
    }
}