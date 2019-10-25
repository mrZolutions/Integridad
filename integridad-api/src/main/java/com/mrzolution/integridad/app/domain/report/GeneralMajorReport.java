package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class GeneralMajorReport {
    private String codeCtble;
    private String fecha;
    private String tipoDocumento;
    private String documento;
    private String descripcion;
    private String cheque;
    private Double deber;
    private Double haber;
    private Double saldos;
    private String periodo;
    
    public GeneralMajorReport(String codeCtble, String fecha, String tipoDocumento, String documento, String descripcion, String cheque, Double deber, Double haber, Double saldos, String periodo) {
        this.codeCtble = codeCtble;
        this.fecha = fecha;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.descripcion = descripcion;
        this.cheque = cheque;
        this.deber = deber;
        this.haber = haber;
        this.saldos = saldos;
        this.periodo = periodo;
    }
}