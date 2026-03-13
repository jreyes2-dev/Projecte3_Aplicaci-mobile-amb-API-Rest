package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.Reputacio;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "client_email")
public class Client extends Usuari {

    // --- Datos de Identificación ---
    @Column(nullable = false, length = 20)
    private String dni;

    @Column(name = "data_caducitat_dni")
    private LocalDate dataCaducitatDni;

   @Lob
    @Column(name = "imatge_dni", columnDefinition = "LONGBLOB")
    private byte[] imatgeDni;

    @Column(name = "nacionalitat")
    private String nacionalitat;

    @Column(name = "adreca")
    private String adreca;

    // Campo que tu backend ya usa en DTO/Service pero faltaba en Entity
    @Column(name = "telefon")
    private String telefon;

    // --- Datos de Conducción ---
    @Column(name = "tipus_carnet_conduir")
    private String tipusCarnetConduir;

    @Column(name = "data_caducitat_carnet")
    private LocalDate dataCaducitatCarnet;

    @Lob
    @Column(name = "imatge_carnet", columnDefinition = "LONGBLOB")
    private byte[] imatgeCarnet;
    
    @Lob
    @Column(name = "foto_perfil", columnDefinition = "LONGBLOB")
    private byte[] fotoPerfil;

    // --- Datos Económicos ---
    @Column(name = "numero_targeta_credit")
    private String numeroTargetaCredit;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'NORMAL'")
    private Reputacio reputacio;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Reserva> reservas = new ArrayList<>();

    public Client() {
        super();
    }

    // --- Getters y Setters ---

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public LocalDate getDataCaducitatDni() { return dataCaducitatDni; }
    public void setDataCaducitatDni(LocalDate dataCaducitatDni) { this.dataCaducitatDni = dataCaducitatDni; }

    public byte[] getImatgeDni() { return imatgeDni; }
    public void setImatgeDni(byte[] imatgeDni) { this.imatgeDni = imatgeDni; }
    
    public String getNacionalitat() { return nacionalitat; }
    public void setNacionalitat(String nacionalitat) { this.nacionalitat = nacionalitat; }

    public String getAdreca() { return adreca; }
    public void setAdreca(String adreca) { this.adreca = adreca; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getTipusCarnetConduir() { return tipusCarnetConduir; }
    public void setTipusCarnetConduir(String tipusCarnetConduir) { this.tipusCarnetConduir = tipusCarnetConduir; }

    public LocalDate getDataCaducitatCarnet() { return dataCaducitatCarnet; }
    public void setDataCaducitatCarnet(LocalDate dataCaducitatCarnet) { this.dataCaducitatCarnet = dataCaducitatCarnet; }

    public byte[] getImatgeCarnet() { return imatgeCarnet; }
    public void setImatgeCarnet(byte[] imatgeCarnet) { this.imatgeCarnet = imatgeCarnet; }

    public String getNumeroTargetaCredit() { return numeroTargetaCredit; }
    public void setNumeroTargetaCredit(String numeroTargetaCredit) { this.numeroTargetaCredit = numeroTargetaCredit; }

    public Reputacio getReputacio() { return reputacio; }
    public void setReputacio(Reputacio reputacio) { this.reputacio = reputacio; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
    
    public byte[] getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(byte[] fotoPerfil) { this.fotoPerfil = fotoPerfil; }


    @Override
    public int hashCode() {
        return Objects.hashCode(this.dni);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Client other = (Client) obj;
        return Objects.equals(this.dni, other.dni);
    }

    @Override
    public String toString() {
        return "Client{" +
                "dni='" + dni + '\'' +
                ", nacionalitat='" + nacionalitat + '\'' +
                ", adreca='" + adreca + '\'' +
                ", telefon='" + telefon + '\'' +
                ", reputacio=" + reputacio +
                '}';
    }
}