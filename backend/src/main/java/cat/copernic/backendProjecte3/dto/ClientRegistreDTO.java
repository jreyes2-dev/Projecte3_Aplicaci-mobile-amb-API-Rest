package cat.copernic.backendProjecte3.dto;

/**
 *
 * @author bharr
 */
/*
 * DTO para la transferencia de datos de registro de cliente.
 */

import java.time.LocalDate;

public class ClientRegistreDTO {

    // --- Datos de Usuari (Login) ---
    private String email;
    private String password;
    private String nomComplet;

    // --- Datos de Identificación ---
    private String dni;
    private LocalDate dataCaducitatDni;
    private String imatgeDni; // URL o Base64 string
    private String nacionalitat;
    private String adreca;
    private String fotoPerfil;

    // --- Datos de Conducción ---
    private String tipusCarnetConduir;
    private LocalDate dataCaducitatCarnet;
    private String imatgeCarnet; // URL o Base64 string

    // --- Datos Económicos ---
    private String numeroTargetaCredit;

    // Constructor vacío
    public ClientRegistreDTO() {
    }

    // --- Getters y Setters ---

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public LocalDate getDataCaducitatDni() { return dataCaducitatDni; }
    public void setDataCaducitatDni(LocalDate dataCaducitatDni) { this.dataCaducitatDni = dataCaducitatDni; }

    public String getImatgeDni() { return imatgeDni; }
    public void setImatgeDni(String imatgeDni) { this.imatgeDni = imatgeDni; }

    public String getNacionalitat() { return nacionalitat; }
    public void setNacionalitat(String nacionalitat) { this.nacionalitat = nacionalitat; }

    public String getAdreca() { return adreca; }
    public void setAdreca(String adreca) { this.adreca = adreca; }
    
    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getTipusCarnetConduir() { return tipusCarnetConduir; }
    public void setTipusCarnetConduir(String tipusCarnetConduir) { this.tipusCarnetConduir = tipusCarnetConduir; }

    public LocalDate getDataCaducitatCarnet() { return dataCaducitatCarnet; }
    public void setDataCaducitatCarnet(LocalDate dataCaducitatCarnet) { this.dataCaducitatCarnet = dataCaducitatCarnet; }

    public String getImatgeCarnet() { return imatgeCarnet; }
    public void setImatgeCarnet(String imatgeCarnet) { this.imatgeCarnet = imatgeCarnet; }

    public String getNumeroTargetaCredit() { return numeroTargetaCredit; }
    public void setNumeroTargetaCredit(String numeroTargetaCredit) { this.numeroTargetaCredit = numeroTargetaCredit; }
}
