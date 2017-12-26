package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

import java.util.List;

@Data
public class TotalesExportacion {
    private float flete_internacional;
    private float seguro_internacional;
    private float gastos_aduaneros;
    private float otros_gastos_transporte;
}
