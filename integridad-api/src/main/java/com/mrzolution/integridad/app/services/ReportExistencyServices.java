/**
 *
 * @author daniel-one
 */
package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Existency;
import com.mrzolution.integridad.app.domain.report.ReportExistency;
import com.mrzolution.integridad.app.repositories.ReportExistencyRepository;
import com.mrzolution.integridad.app.repositories.UserClientRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportExistencyServices {
    @Autowired
    ReportExistencyRepository reportExistencyRepository;
    
    @Autowired
    UserClientRepository userClientRepository;
    
    public List<ReportExistency>getProductsByUserClientId(UUID userClientId) {
        log.info("ReportExistencyServices getProductsByUserClientId id: {}", userClientId);
        Iterable<Existency> existencies = reportExistencyRepository.findProductsByUserClientId(userClientId);
        List<ReportExistency> reportExistencyList = new ArrayList<>();
        
        existencies.forEach(existency->{
            ReportExistency saleReport = new ReportExistency(existency.getCode(),existency.getName(), existency.getCost(), existency.getMaxMin(), existency.getQuantity());
            reportExistencyList.add(saleReport);
        });
        return reportExistencyList;
    }
}
