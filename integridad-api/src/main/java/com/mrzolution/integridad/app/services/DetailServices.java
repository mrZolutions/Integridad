package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.Detail;
import com.mrzolution.integridad.app.repositories.DetailRepository;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author daniel-one
 */

@Slf4j
@Component
public class DetailServices {
    @Autowired
    DetailRepository detailRepository;
    
    public Iterable<Detail> getDetailsOfBillsByUserClientId(UUID userClientId) {
        Iterable<Detail> details = detailRepository.findDetailsOfBillsByUserClientId(userClientId);
        details.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.getBill().setFatherListToNull();
            detail.getBill().setListsNull();
        });
        log.info("DetailServices getDetailByUserClientId DONE: {}", userClientId);
        return details;
    }
}