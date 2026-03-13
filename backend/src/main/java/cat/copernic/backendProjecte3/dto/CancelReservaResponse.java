/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backendProjecte3.dto;

import java.math.BigDecimal;

/**
 *
 * @author HAMZA
 */
public class CancelReservaResponse {
    private Long idReserva;
    private BigDecimal refundAmount;
    private String message;

    public CancelReservaResponse(Long idReserva, BigDecimal refundAmount, String message) {
        this.idReserva = idReserva;
        this.refundAmount = refundAmount;
        this.message = message;
    }

    public Long getIdReserva() { return idReserva; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public String getMessage() { return message; }
}