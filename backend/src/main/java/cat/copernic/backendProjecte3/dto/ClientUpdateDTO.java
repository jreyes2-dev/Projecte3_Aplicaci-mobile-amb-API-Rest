package cat.copernic.backendProjecte3.dto;

import java.time.LocalDate;

public class ClientUpdateDTO {

    private String nomComplet;
    private String telefon;
    private String adreca;
    private String nacionalitat;
    private String numeroTargetaCredit;
    private LocalDate dataCaducitatDni;
    private String tipusCarnetConduir;
    private LocalDate dataCaducitatCarnet;

    // RF04: permitir actualizar documentación
    private String imatgeDni;
    private String imatgeCarnet;
    private String fotoPerfil;

    public String getNomComplet() { return nomComplet; }
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getAdreca() { return adreca; }
    public void setAdreca(String adreca) { this.adreca = adreca; }

    public String getNacionalitat() { return nacionalitat; }
    public void setNacionalitat(String nacionalitat) { this.nacionalitat = nacionalitat; }

    public String getNumeroTargetaCredit() { return numeroTargetaCredit; }
    public void setNumeroTargetaCredit(String numeroTargetaCredit) { this.numeroTargetaCredit = numeroTargetaCredit; }

    public LocalDate getDataCaducitatDni() { return dataCaducitatDni; }
    public void setDataCaducitatDni(LocalDate dataCaducitatDni) { this.dataCaducitatDni = dataCaducitatDni; }

    public String getTipusCarnetConduir() { return tipusCarnetConduir; }
    public void setTipusCarnetConduir(String tipusCarnetConduir) { this.tipusCarnetConduir = tipusCarnetConduir; }

    public LocalDate getDataCaducitatCarnet() { return dataCaducitatCarnet; }
    public void setDataCaducitatCarnet(LocalDate dataCaducitatCarnet) { this.dataCaducitatCarnet = dataCaducitatCarnet; }

    public String getImatgeDni() { return imatgeDni; }
    public void setImatgeDni(String imatgeDni) { this.imatgeDni = imatgeDni; }

    public String getImatgeCarnet() { return imatgeCarnet; }
    public void setImatgeCarnet(String imatgeCarnet) { this.imatgeCarnet = imatgeCarnet; }
    
    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

   
}