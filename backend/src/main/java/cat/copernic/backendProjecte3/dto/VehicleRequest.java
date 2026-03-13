package cat.copernic.backendProjecte3.dto;

import java.math.BigDecimal;

public class VehicleRequest {

    private String matricula;
    private String marca;
    private String model;
    private String variant;
    private BigDecimal preuHora;
    private String fotoBase64; // Imagen en Base64
    private String potencia;
    private String color;
    private Integer limitQuilometratge;
    private BigDecimal fiancaEstandard;
    private Integer minDiesLloguer;
    private Integer maxDiesLloguer;

    // Constructor vacío
    public VehicleRequest() {
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
