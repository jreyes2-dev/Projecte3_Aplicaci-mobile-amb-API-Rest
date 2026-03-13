package cat.copernic.backendProjecte3.business;

import cat.copernic.backendProjecte3.config.PasswordHasher;
import cat.copernic.backendProjecte3.entities.Usuari;
import cat.copernic.backendProjecte3.enums.UserRole;
import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import cat.copernic.backendProjecte3.exceptions.InvalidEmailException;
import cat.copernic.backendProjecte3.exceptions.InvalidResetTokenException;
import cat.copernic.backendProjecte3.repository.UsuariRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLogic {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private EmailService emailService;

    public Optional<UserRole> login(String email, String rawPassword) throws AccesDenegatException {

        UserRole ret = UserRole.NONE;

        Usuari user = usuariRepository.findByEmail(email)
                .orElseThrow(() -> new AccesDenegatException("Usuari no existeix"));

        if (PasswordHasher.check(rawPassword, user.getPassword())) {
            ret = user.getRol();
        } else {
            throw new AccesDenegatException("Bad Password");
        }

        return Optional.of(ret);
    }

    public Optional<UserRole> getRole(String email) throws AccesDenegatException {

        Usuari user = usuariRepository.findByEmail(email)
                .orElseThrow(() -> new AccesDenegatException("Usuari no existeix"));

        return Optional.of(user.getRol());
    }

    public void recoverPassword(String email) {

        validateEmail(email);

        Optional<Usuari> userOptional = usuariRepository.findByEmail(email.trim().toLowerCase());

        if (userOptional.isEmpty()) {
            return;
        }

        Usuari user = userOptional.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiry(expiry);

        usuariRepository.save(user);

        emailService.sendPasswordRecoveryEmail(
                user.getEmail(),
                user.getNomComplet(),
                token
        );
    }

    public void resetPassword(String token, String newPassword) {

        if (token == null || token.isBlank()) {
            throw new InvalidResetTokenException("Recovery token is required");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new InvalidResetTokenException("New password is required");
        }

        if (newPassword.length() < 6) {
            throw new InvalidResetTokenException("Password must have at least 6 characters");
        }

        Usuari user = usuariRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new InvalidResetTokenException("Invalid recovery token"));

        if (user.getResetPasswordExpiry() == null || user.getResetPasswordExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Recovery token expired");
        }

        user.setPassword(PasswordHasher.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiry(null);

        usuariRepository.save(user);
    }

    private void validateEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email is required");
        }

        try {
            InternetAddress address = new InternetAddress(email.trim());
            address.validate();
        } catch (AddressException e) {
            throw new InvalidEmailException("Invalid email format");
        }
    }
}