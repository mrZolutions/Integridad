package com.mrzolution.integridad.app.domain.ebill;

import lombok.Data;

@Data
public class Credito {
    private String fecha_vencimiento;
    private float monto;
}
