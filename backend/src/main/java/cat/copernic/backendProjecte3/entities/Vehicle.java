package cat.copernic.backendProjecte3.entities;

import cat.copernic.backendProjecte3.enums.EstatVehicle;
import cat.copernic.backendProjecte3.enums.TipusVehicle;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entitat que representa un vehicle dins del sistema.
 *
 * Aquesta classe està mapejada a la taula "vehicle" de la base de dades
 * mitjançant JPA/Hibernate.
 *
 * Conté tota la informació relacionada amb un vehicle disponible per al
 * lloguer, incloent dades tècniques, econòmiques i informació sobre la seva
 * disponibilitat.
 *
 * També manté la relació amb les reserves associades.
 */
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @Column(length = 20)
    private String matricula;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String variant;

    // --- CAMPOS MODIFICADOS PARA IMAGEN BLOB ---
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fotoBinario;  // NUEVO: Almacena la imagen como BLOB

    private String fotoUrl;  // Lo mantenemos por compatibilidad

    private String potencia;
    private String color;
    private Integer limitQuilometratge;

    @Column(precision = 10, scale = 2)
    private BigDecimal preuHora;

    @Column(precision = 10, scale = 2)
    private BigDecimal fiancaEstandard;

    private Integer minDiesLloguer;
    private Integer maxDiesLloguer;

    @Enumerated(EnumType.STRING)
    private TipusVehicle tipusVehicle;

    @Enumerated(EnumType.STRING)
    private EstatVehicle estatVehicle;

    @Column(columnDefinition = "TEXT")
    private String comentarisPrivats;

    private String rutaDocumentacioPrivada;

    @OneToMany(mappedBy = "vehicle")
    private List<Reserva> reservas = new ArrayList<>();

    public Vehicle() {
    }

    // GETTERS Y SETTERS
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    // NUEVOS GETTERS Y SETTERS PARA fotoBinario
    public byte[] getFotoBinario() {
        return fotoBinario;
    }

    public void setFotoBinario(byte[] fotoBinario) {
        this.fotoBinario = fotoBinario;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getPotencia() {
        return potencia;
    }

    public void setPotencia(String potencia) {
        this.potencia = potencia;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getLimitQuilometratge() {
        return limitQuilometratge;
    }

    public void setLimitQuilometratge(Integer limitQuilometratge) {
        this.limitQuilometratge = limitQuilometratge;
    }

    public BigDecimal getPreuHora() {
        return preuHora;
    }

    public void setPreuHora(BigDecimal preuHora) {
        this.preuHora = preuHora;
    }

    public BigDecimal getFiancaEstandard() {
        return fiancaEstandard;
    }

    public void setFiancaEstandard(BigDecimal fiancaEstandard) {
        this.fiancaEstandard = fiancaEstandard;
    }

    public Integer getMinDiesLloguer() {
        return minDiesLloguer;
    }

    public void setMinDiesLloguer(Integer minDiesLloguer) {
        this.minDiesLloguer = minDiesLloguer;
    }

    public Integer getMaxDiesLloguer() {
        return maxDiesLloguer;
    }

    public void setMaxDiesLloguer(Integer maxDiesLloguer) {
        this.maxDiesLloguer = maxDiesLloguer;
    }

    public TipusVehicle getTipusVehicle() {
        return tipusVehicle;
    }

    public void setTipusVehicle(TipusVehicle tipusVehicle) {
        this.tipusVehicle = tipusVehicle;
    }

    public EstatVehicle getEstatVehicle() {
        return estatVehicle;
    }

    public void setEstatVehicle(EstatVehicle estatVehicle) {
        this.estatVehicle = estatVehicle;
    }

    public String getComentarisPrivats() {
        return comentarisPrivats;
    }

    public void setComentarisPrivats(String comentarisPrivats) {
        this.comentarisPrivats = comentarisPrivats;
    }

    public String getRutaDocumentacioPrivada() {
        return rutaDocumentacioPrivada;
    }

    public void setRutaDocumentacioPrivada(String rutaDocumentacioPrivada) {
        this.rutaDocumentacioPrivada = rutaDocumentacioPrivada;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.matricula);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vehicle)) {
            return false;
        }
        Vehicle other = (Vehicle) obj;
        return Objects.equals(this.matricula, other.matricula);
    }

    @Override
    public String toString() {
        return "Vehicle{"
                + "matricula=" + matricula
                + ", marca=" + marca
                + ", model=" + model
                + ", variant=" + variant
                + ", preuHora=" + preuHora
                + '}';
    }

}
