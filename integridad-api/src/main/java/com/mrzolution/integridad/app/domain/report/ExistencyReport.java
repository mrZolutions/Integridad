package com.mrzolution.integridad.app.domain.report;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;

/**
 *
 * @author mrzolutions-daniel
 */
@Entity
@Immutable
@Table(name = "vista_existency")
public class ExistencyReport {
    private UUID userClientId;
    private String code;
    private String name;
    private Double cost;
    private long maxMin;
    private long quantity;
    
    public ExistencyReport(UUID userClientId, String code, String name, Double cost, long maxMin, long quantity) {
        this.code = code;
        this.name = name;
        this.cost = cost;
        this.maxMin = maxMin;
        this.quantity = quantity;
    }
}
