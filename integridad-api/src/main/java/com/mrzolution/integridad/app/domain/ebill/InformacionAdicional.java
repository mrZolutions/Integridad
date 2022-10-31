package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InformacionAdicional {
    private String info1;
    private String info2;
    private String observ;
    private String placa;
    private String telefono;
    private String puntoEmision;
    private String ruc;
    private String email;
    private String name;
    private String direccion;
}
