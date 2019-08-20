package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class EspecificMajorReport {
    private String fecha;
    private String tipoDocumento;
    private String documento;
    private String descripcion;
    private String cheque;
    private Double deber;
    private Double haber;
    private Double saldos;
    
    public EspecificMajorReport(String fecha, String tipoDocumento, String documento, String descripcion, String cheque, Double deber, Double haber, Double saldos) {
        this.fecha = fecha;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.descripcion = descripcion;
        this.cheque = cheque;
        this.deber = deber;
        this.haber = haber;
        this.saldos = saldos;
    }
}