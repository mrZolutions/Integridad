package com.mrzolution.integridad.app.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mrzolution.integridad.app.domain.*;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductWrapper {
    Product product;
    String removeReason;
    Long date;
    UUID userId;
}
