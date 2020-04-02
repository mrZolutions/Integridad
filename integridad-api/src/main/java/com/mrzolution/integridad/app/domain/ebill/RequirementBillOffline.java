package com.mrzolution.integridad.app.domain.ebill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.BillOffline;
import com.mrzolution.integridad.app.domain.ComprobanteCobro;
import com.mrzolution.integridad.app.domain.DailybookCi;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequirementBillOffline {
    DailybookCi dailybookCi;
    ComprobanteCobro comprobanteCobro;
    BillOffline bill;
}
