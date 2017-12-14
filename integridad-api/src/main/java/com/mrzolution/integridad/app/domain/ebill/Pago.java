package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

@Data
public class Pago {
    private String fecha;
    private String medio;
    private float total;
    private PagoPropiedades propiedades;
}
