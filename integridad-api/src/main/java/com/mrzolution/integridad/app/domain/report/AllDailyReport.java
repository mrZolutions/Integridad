package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class AllDailyReport {
    private String fecha;
    private String tipoDocumento;
    private String clienteProveedor;
    private String descripcion;
    private String numero;
    private String numeroFactura;
    private Double deber;
    private Double haber;

    public AllDailyReport(String fecha, String tipoDocumento, String clienteProveedor, String descripcion,
                          String numero, String numeroFactura, Double deber, Double haber) {
        this.fecha = fecha;
        this.tipoDocumento = tipoDocumento;
        this.clienteProveedor = clienteProveedor;
        this.descripcion = descripcion;
        this.numero = numero;
        this.numeroFactura = numeroFactura;
        this.deber = deber;
        this.haber = haber;
    }
}