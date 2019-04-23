package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.DetailCellar;
import com.mrzolution.integridad.app.repositories.DetailCellarRepository;
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
public class DetailCellarServices {
    @Autowired
    DetailCellarRepository detailCellarRepository;
    
    public Iterable<DetailCellar> getDetailsOfCellarsByUserClientId(UUID userClientId) {
        log.info("DetailCellarServices getDetailByUserClientId: {}", userClientId);
        Iterable<DetailCellar> details = detailCellarRepository.findDetailsOfCellarsByUserClientId(userClientId);
        details.forEach(detail -> {
            detail.setListsNull();
            detail.setFatherListToNull();
            detail.getProduct().setFatherListToNull();
            detail.getProduct().setListsNull();
            detail.getCellar().setFatherListToNull();
            detail.getCellar().setListsNull();
        });
        return details;
    }
}