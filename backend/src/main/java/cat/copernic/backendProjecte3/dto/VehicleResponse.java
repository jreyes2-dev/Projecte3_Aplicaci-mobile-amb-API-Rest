package cat.copernic.backendProjecte3.dto;

import cat.copernic.backendProjecte3.entities.Vehicle;
import java.math.BigDecimal;
import java.util.Base64;

public class VehicleResponse {

    private String matricula;
    private String marca;
    private String model;
    private String variant;
    private BigDecimal preuHora;
    private String fotoBase64; // NUEVO: Imagen en Base64
    private String fotoUrl;     // Lo mantenemos por compatibilidad
    private String potencia;
    private String color;
    private Integer limitQuilometratge;
    private BigDecimal fiancaEstandard;
    private Integer minDiesLloguer;
    private Integer maxDiesLloguer;

    // Constructor por defecto
    public VehicleResponse() {
    }

    // Constructor desde entidad
    public VehicleResponse(Vehicle vehicle) {
        this.matricula = vehicle.getMatricula();
        this.marca = vehicle.getMarca();
        this.model = vehicle.getModel();
        this.variant = vehicle.getVariant();
        this.preuHora = vehicle.getPreuHora();
        this.fotoUrl = vehicle.getFotoUrl();
        this.potencia = vehicle.getPotencia();
        this.color = vehicle.getColor();
        this.limitQuilometratge = vehicle.getLimitQuilometratge();
        this.fiancaEstandard = vehicle.getFiancaEstandard();
        this.minDiesLloguer = vehicle.getMinDiesLloguer();
        this.maxDiesLloguer = vehicle.getMaxDiesLloguer();

        // Convertir byte[] a Base64 para JSON
        if (vehicle.getFotoBinario() != null) {
            this.fotoBase64 = Base64.getEncoder().encodeToString(vehicle.getFotoBinario());
        }
    }

    // Getters y Setters
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

    public BigDecimal getPreuHora() {
        return preuHora;
    }

    public void setPreuHora(BigDecimal preuHora) {
        this.preuHora = preuHora;
    }

    public String getFotoBase64() {
        return fotoBase64;
    }

    public void setFotoBase64(String fotoBase64) {
        this.fotoBase64 = fotoBase64;
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
}
