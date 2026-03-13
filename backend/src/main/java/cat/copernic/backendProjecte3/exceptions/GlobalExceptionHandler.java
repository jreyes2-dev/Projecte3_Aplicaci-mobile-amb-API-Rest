package cat.copernic.backendProjecte3.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccesDenegatException.class)
    public ResponseEntity<String> handleAccess(AccesDenegatException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(ReservaNoCancelableException.class)
    public ResponseEntity<String> handleReservaNoCancelable(ReservaNoCancelableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ReservaDatesNoValidsException.class)
    public ResponseEntity<String> handleReservaDates(ReservaDatesNoValidsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(VehicleNoDisponibleException.class)
    public ResponseEntity<String> handleVehicleNoDisponible(VehicleNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DadesNoTrobadesException.class)
    public ResponseEntity<String> handleNotFound(DadesNoTrobadesException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<String> handleInvalidEmail(InvalidEmailException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<String> handleInvalidResetToken(InvalidResetTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error intern del servidor");
    }
}
