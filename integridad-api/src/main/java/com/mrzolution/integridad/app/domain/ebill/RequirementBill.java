package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.Bill;
import com.mrzolution.integridad.app.domain.ComprobanteCobro;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequirementBill {
    Requirement requirement;
    ComprobanteCobro comprobanteCobro;
    Bill bill;
}
