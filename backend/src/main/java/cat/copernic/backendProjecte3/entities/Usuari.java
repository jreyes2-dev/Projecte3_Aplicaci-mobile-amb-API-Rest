/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.UserRole;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

/**
 *
 * @author manel
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuari")
public class Usuari {

    @Id
    @Column(length = 100)
    private String email; // Será el username [cite: 120]

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "nom_complet", nullable = false)
    private String nomComplet; // Requerido por 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole rol = UserRole.NONE;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_expiry")
    private LocalDateTime resetPasswordExpiry;

    // Constructor con campos
    public Usuari(String email) {
        this.email = email;
    }

    public Usuari() {
    }

    // --- Getters y Setters ---
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public UserRole getRol() {
        return rol;
    }

    public void setRol(UserRole rol) {
        this.rol = rol;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordExpiry() {
        return resetPasswordExpiry;
    }

    public void setResetPasswordExpiry(LocalDateTime resetPasswordExpiry) {
        this.resetPasswordExpiry = resetPasswordExpiry;
    }

    // Métodos de seguridad/autoridad
    public List<UserRole> getAuthorities() {
        List<UserRole> roles = new ArrayList<>();
        roles.add(this.getRol());
        return roles;
    }

    public String getUsername() {
        return this.email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Usuari usuari = (Usuari) o;
        return Objects.equals(email, usuari.email);
    }

    @Override
    public String toString() {
        return "Usuari{" + "email=" + email + ", rol=" + rol + '}';
    }
}
