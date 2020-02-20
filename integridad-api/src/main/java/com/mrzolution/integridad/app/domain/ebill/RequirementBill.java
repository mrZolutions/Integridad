package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.Bill;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequirementBill {
    Requirement requirement;
    Bill bill;
}
